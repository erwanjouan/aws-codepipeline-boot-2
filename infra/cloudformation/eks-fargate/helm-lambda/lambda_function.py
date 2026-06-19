import boto3
import cfnresponse
import logging
import os
import subprocess
import traceback

k8s_user_namespace = os.environ["K8S_NAMESPACE"]
project_deployment_name = os.environ["PROJECT_DEPLOYMENT_NAME"]
region = os.environ["AWS_DEFAULT_REGION"]
account_id = os.environ["AWS_ACCOUNT_ID"]

ec2 = boto3.client('ec2', region_name=region)

logger = logging.getLogger()
logger.setLevel(logging.INFO)


def shell_process(args):
    logger.info(' '.join(args))
    process = subprocess.Popen(
        args,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        universal_newlines=True
    )
    stdout, stderr = process.communicate()
    log(stdout, stderr)
    if process.returncode != 0:
        raise Exception(stderr)
    return stdout


def log(stdout, stderr):
    log_(stdout, 'stdout')
    log_(stderr, 'stderr')


def log_(input, input_type):
    if input is not None and len(input) > 0:
        logger.info(input_type + ' {}'.format(input))


def kubectl_apply(template_file):
    kubectl_env(template_file, 'apply')


def kubectl_delete(template_file):
    kubectl_env(template_file, 'delete')


def kubectl_env(template_file, mode):
    logger.info(' '.join(['kubectl', mode, '-f', template_file]))

    process1 = subprocess.Popen(
        ['cat', template_file],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True)
    process2 = subprocess.Popen(
        ['envsubst'],
        stdin=process1.stdout,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True)
    process3 = subprocess.Popen(
        ['kubectl', mode, '-f', '-'],
        stdin=process2.stdout,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True)

    stdout, stderr = process3.communicate()
    log(stdout, stderr)
    if process3.returncode != 0:
        raise Exception(stderr)
    return stdout


def set_kube_config():
    shell_process([
        'aws', 'eks', '--region', region,
        'update-kubeconfig',
        '--name', project_deployment_name])


def install_crds():
    shell_process(
        ['kubectl', 'apply', '-k',
         'github.com/aws/eks-charts//stable/aws-load-balancer-controller/crds?ref=master'])


def get_vpc_id():
    response = ec2.describe_vpcs(
        Filters=[
            {
                'Name': 'isDefault',
                'Values': [
                    'false',
                ]
            },
        ]
    )
    return response['Vpcs'][0]['VpcId']


def install_aws_load_balancer_controller():
    lb_controller_service_account()
    install_crds()
    vpc_id = get_vpc_id()
    shell_process(['helm', 'repo', 'add', 'eks', 'https://aws.github.io/eks-charts'])
    shell_process(['helm', 'repo', 'update'])
    shell_process(['helm', 'install', 'aws-load-balancer-controller', 'eks/aws-load-balancer-controller',
                   '--namespace', k8s_user_namespace,
                   '--set', 'clusterName=' + project_deployment_name,
                   '--set', 'serviceAccount.create=false',
                   '--set', 'region=' + region,
                   '--set', 'vpcId=' + vpc_id,
                   '--set', 'serviceAccount.name=aws-load-balancer-controller'
                   ])
    shell_process(['sleep', '60'])  # wait for aws-load-balancer-controller to be ready


def patch_coredns_deployment():
    shell_process(['kubectl', 'patch', 'deployment', 'coredns',
                   '-n', 'kube-system',
                   '--type', 'json',
                   '--patch-file', './k8s/01_patch_coredns.json'])


def restart_coredns_deployment():
    shell_process(['kubectl', 'rollout', 'restart',
                   '-n', 'kube-system',
                   'deployment/coredns'])


def patch_aws_auth_configmap():
    tmp_file = '/tmp/aws-auth_configmap.json'
    with open(tmp_file, 'w') as fd:
        process1 = subprocess.Popen(
            ['kubectl', 'get', 'cm', 'aws-auth', '-n', 'kube-system', '-o', 'json'],
            stdout=fd,
            text=True)
        stdout, stderr = process1.communicate()
        log(stdout, stderr)

    map_users = "- userarn: arn:aws:iam::" + account_id + ":user/devops\n  username: devops\n  groups:\n    - system:masters\n"
    map_users += "- userarn: arn:aws:iam::" + account_id + ":user/terraform\n  username: terraform\n  groups:\n    - system:masters"

    process2 = subprocess.Popen(
        ['jq', '--arg', 'add', map_users, ".data.mapUsers = $add", tmp_file],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True)

    process3 = subprocess.Popen(
        ['kubectl', 'apply', '-f', '-'],
        stdin=process2.stdout,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True)

    stdout, stderr = process3.communicate()
    log(stdout, stderr)
    if process3.returncode != 0:
        raise Exception(stderr)
    return stdout


def create_user_namespace():
    kubectl_apply('k8s/02_user_namespace.yml')


def delete_user_namespace():
    kubectl_delete('k8s/02_user_namespace.yml')


def lb_controller_service_account():
    kubectl_apply('k8s/03_lb_controller_service_account.yml')


def application_deployment():
    kubectl_apply('k8s/08_app_deployment.yml')


def purge_alb_security_groups():
    cluster_name_to_purge = project_deployment_name
    sgs = ec2.describe_security_groups(Filters=[
        {
            'Name': 'tag:elbv2.k8s.aws/cluster',
            'Values': [cluster_name_to_purge]
        },
    ])
    if sgs['SecurityGroups'] is not None:
        for sg in sgs['SecurityGroups']:
            try:
                resp = ec2.delete_security_group(GroupId=sg['GroupId'])
                logger.info("[OK] Deleted security group %s\n" % resp)
            except Exception as e:
                logger.info("Exception when deleting security group: %s\n" % e)


def create_cloudwatch_logs_config():
    kubectl_apply('k8s/06_aws_observability_namespace.yml')
    kubectl_apply('k8s/07_aws_logging_cloudwatch_configmap.yml')


def create_ingress():
    kubectl_apply('k8s/05_ingress.yml')


def create_service():
    kubectl_apply('k8s/04_service.yml')


def delete_load_balancer():
    elbv2 = boto3.client('elbv2')
    response = elbv2.describe_load_balancers()
    logger.info(response)
    for lb in response['LoadBalancers']:
        if 'k8s-awscodep' in lb['LoadBalancerName']:
            load_balancer_arn = lb['LoadBalancerArn']
            elbv2.delete_load_balancer(LoadBalancerArn=load_balancer_arn)
            logger.info('deleted {}'.format(load_balancer_arn))


def delete_target_group():
    elbv2 = boto3.client('elbv2')
    response = elbv2.describe_target_groups()
    logger.info(response)
    for tg in response['TargetGroups']:
        if 'k8s-awscodep' in tg['TargetGroupName']:
            target_group_arn = tg['TargetGroupArn']
            elbv2.delete_target_group(TargetGroupArn=target_group_arn)
            logger.info('deleted {}'.format(target_group_arn))


def remove_load_balancer_sg_rule():
    cluster_name_to_purge = project_deployment_name
    sgs = ec2.describe_security_groups(Filters=[
        {
            'Name': 'tag:aws:eks:cluster-name',
            'Values': [cluster_name_to_purge]
        },
        {
            'Name': 'tag:kubernetes.io/cluster/' + cluster_name_to_purge,
            'Values': ['owned']
        }
    ])
    if sgs['SecurityGroups'] is not None:
        for sg in sgs['SecurityGroups']:
            try:
                logger.info('{} eligible for rule edition'.format(sg['GroupId']))
                rules = ec2.describe_security_group_rules(
                    Filters=[
                        {
                            'Name': 'group-id',
                            'Values': [
                                sg['GroupId']
                            ]
                        },
                    ]
                )
                for rule in rules['SecurityGroupRules']:
                    if rule['IsEgress'] == False and rule['IpProtocol'] == 'tcp' and rule['FromPort'] == 8080 and rule[
                        'ToPort'] == 8080:
                        logger.info('removing {}'.format(rule))
                        ec2.revoke_security_group_ingress(GroupId=sg['GroupId'],
                                                          SecurityGroupRuleIds=[rule['SecurityGroupRuleId']])
            except Exception as e:
                logger.info("Exception when deleting security group rule: %s\n" % e)


def create_pod_auto_scaler():
    k8s_deployment_name = 'deployment-' + project_deployment_name
    min_number_of_replicas = os.environ["NUMBER_OF_REPLICAS"]
    max_number_of_replicas = '6'
    target_cpu_percent = '50'
    # Deploy Hpa
    shell_process(['kubectl', 'autoscale', 'deployment',
                   k8s_deployment_name,
                   '-n', k8s_user_namespace,
                   '--cpu-percent=' + target_cpu_percent,
                   '--min=' + min_number_of_replicas,
                   '--max=' + max_number_of_replicas])
    # Deploy the Metrics Server
    shell_process(
        ['kubectl', 'apply', '-f',
         'https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.6.4/components.yaml'])


def handler(_event, _context):
    response_data = {}
    try:
        logger.info('got event {}'.format(_event))
        if _event['RequestType'] is not None and _event['RequestType'] in ['Create']:
            set_kube_config()
            patch_aws_auth_configmap()
            patch_coredns_deployment()
            create_cloudwatch_logs_config()
            restart_coredns_deployment()
            create_user_namespace()
            install_aws_load_balancer_controller()
            create_service()
            create_ingress()
            application_deployment()
            create_pod_auto_scaler()
        if _event['RequestType'] is not None and _event['RequestType'] in ['Update']:
            set_kube_config()
            application_deployment()
        if _event['RequestType'] is not None and _event['RequestType'] in ['Delete']:
            delete_load_balancer()
            shell_process(['sleep', '20'])  # wait for load-balancer deletion
            delete_target_group()
            remove_load_balancer_sg_rule()
            shell_process(['sleep', '20'])  # wait for sg rule deletion
            purge_alb_security_groups()
        cfnresponse.send(_event, _context, cfnresponse.SUCCESS, response_data, 'CustomResourcePhysicalID')
    except Exception:
        # Sending FAILED signal to CloudFormation
        logger.info('Exception {}'.format(traceback.format_exc()))
        cfnresponse.send(_event, _context, cfnresponse.FAILED, response_data, 'CustomResourcePhysicalID')
    return f"ok"

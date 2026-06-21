import { BuildSpec, ComputeType, LinuxBuildImage, PipelineProject } from 'aws-cdk-lib/aws-codebuild';
import { LogGroup } from 'aws-cdk-lib/aws-logs';
import { Construct } from 'constructs';
import { Constants } from '../../constants';
import { CodeBuildRole } from './code-build-role';

export class AsgDeployBuild extends Construct {
    project: PipelineProject;

    constructor(scope: Construct, id: string, codeBuildRole: CodeBuildRole) {
        super(scope, id);

        const logGroup = new LogGroup(this, 'LogGroup');
        const asgName = process.env.PROJECT_DEPLOYMENT_NAME!;
        const region = Constants.DEFAULT_REGION;
        const crossAccountRoleArn = `arn:aws:iam::${Constants.WORKLOAD_ACCOUNT_ID}:role/${Constants.ASG_CROSS_ACCOUNT_ROLE_NAME}`;

        const buildSpec = BuildSpec.fromObject({
            version: '0.2',
            phases: {
                pre_build: {
                    commands: [
                        `CREDS=$(aws sts assume-role --role-arn ${crossAccountRoleArn} --role-session-name asg-rolling-deploy)`,
                        `export AWS_ACCESS_KEY_ID=$(echo $CREDS | jq -r .Credentials.AccessKeyId)`,
                        `export AWS_SECRET_ACCESS_KEY=$(echo $CREDS | jq -r .Credentials.SecretAccessKey)`,
                        `export AWS_SESSION_TOKEN=$(echo $CREDS | jq -r .Credentials.SessionToken)`,
                    ],
                },
                build: {
                    commands: [
                        `REFRESH_ID=$(aws autoscaling start-instance-refresh --auto-scaling-group-name ${asgName} --region ${region} --preferences '{"MinHealthyPercentage":50,"InstanceWarmup":300}' --query InstanceRefreshId --output text)`,
                        `echo "Instance refresh started: $REFRESH_ID"`,
                        // Poll until the refresh completes
                        [
                            `while true; do`,
                            `  STATUS=$(aws autoscaling describe-instance-refreshes --auto-scaling-group-name ${asgName} --region ${region} --instance-refresh-ids $REFRESH_ID --query 'InstanceRefreshes[0].Status' --output text);`,
                            `  echo "Refresh status: $STATUS";`,
                            `  if [ "$STATUS" = "Successful" ]; then echo "Instance refresh succeeded"; break; fi;`,
                            `  if [ "$STATUS" = "Failed" ] || [ "$STATUS" = "Cancelled" ]; then echo "Instance refresh $STATUS"; exit 1; fi;`,
                            `  sleep 30;`,
                            `done`,
                        ].join(' '),
                    ],
                },
            },
        });

        this.project = new PipelineProject(this, 'Project', {
            projectName: 'asg-rolling-deploy',
            role: codeBuildRole.role,
            environment: {
                computeType: ComputeType.SMALL,
                buildImage: LinuxBuildImage.STANDARD_7_0,
            },
            buildSpec,
            logging: { cloudWatch: { logGroup } },
        });
    }
}

import { BuildSpec, ComputeType, LinuxBuildImage, PipelineProject } from 'aws-cdk-lib/aws-codebuild';
import { LogGroup } from 'aws-cdk-lib/aws-logs';
import { Construct } from 'constructs';
import { Constants } from '../../constants';
import { CodeBuildRole } from './code-build-role';

export class EcsDeployBuild extends Construct {
    project: PipelineProject;

    constructor(scope: Construct, id: string, codeBuildRole: CodeBuildRole) {
        super(scope, id);

        const logGroup = new LogGroup(this, 'LogGroup');
        const clusterName = process.env.PROJECT_DEPLOYMENT_NAME!;
        const region = Constants.DEFAULT_REGION;
        const crossAccountRoleArn = `arn:aws:iam::${Constants.WORKLOAD_ACCOUNT_ID}:role/${Constants.FARGATE_CROSS_ACCOUNT_ROLE_NAME}`;

        const buildSpec = BuildSpec.fromObject({
            version: '0.2',
            phases: {
                pre_build: {
                    commands: [
                        // Read image URI produced by the build stage
                        `export IMAGE_URI=$(jq -r '.[0].imageUri' imagedefinitions.json)`,
                        // Assume cross-account role to access the workload account
                        `CREDS=$(aws sts assume-role --role-arn ${crossAccountRoleArn} --role-session-name ecs-deploy)`,
                        `export AWS_ACCESS_KEY_ID=$(echo $CREDS | jq -r .Credentials.AccessKeyId)`,
                        `export AWS_SECRET_ACCESS_KEY=$(echo $CREDS | jq -r .Credentials.SecretAccessKey)`,
                        `export AWS_SESSION_TOKEN=$(echo $CREDS | jq -r .Credentials.SessionToken)`,
                    ],
                },
                build: {
                    commands: [
                        // Troubleshoot identity
                        `aws sts get-caller-identity`,
                        // Fetch the live task definition, clear the nginx bootstrap command override,
                        // and substitute the new Spring Boot image URI before registering.
                        `TASK_DEF_JSON=$(aws ecs describe-task-definition --task-definition ${clusterName} --region ${region} | jq --arg img "$IMAGE_URI" '.taskDefinition | del(.taskDefinitionArn,.revision,.status,.requiresAttributes,.placementConstraints,.compatibilities,.registeredAt,.registeredBy) | .containerDefinitions[0].image = $img | del(.containerDefinitions[0].command)')`,
                        `echo TASK_DEF_JSON $TASK_DEF_JSON`,
                        `NEW_TASK_DEF_ARN=$(aws ecs register-task-definition --region ${region} --cli-input-json "$TASK_DEF_JSON" | jq -r .taskDefinition.taskDefinitionArn)`,
                        `echo NEW_TASK_DEF_ARN $NEW_TASK_DEF_ARN`,
                        `aws ecs update-service --cluster ${clusterName} --service ${clusterName} --region ${region} --task-definition $NEW_TASK_DEF_ARN`,
                    ],
                },
                post_build: {
                    commands: [
                        `aws ecs wait services-stable --cluster ${clusterName} --service ${clusterName} --region ${region}`,
                    ],
                },
            },
        });

        this.project = new PipelineProject(this, 'Project', {
            projectName: 'fargate-ecs-deploy',
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

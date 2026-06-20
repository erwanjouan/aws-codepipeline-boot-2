import { BuildSpec, Cache, ComputeType, LinuxBuildImage, PipelineProject } from 'aws-cdk-lib/aws-codebuild';
import { LogGroup } from 'aws-cdk-lib/aws-logs';
import { Construct } from 'constructs';
import { Constants } from '../../constants';
import { ArtifactBucket } from './artifact-bucket';
import { CodeBuildRole } from './code-build-role';
import { ImageRegistry } from './image-registry';

export class CodeBuildApp extends Construct {
    project: PipelineProject;

    constructor(scope: Construct, id: string, artifactBucket: ArtifactBucket, imageRegistry: ImageRegistry, codeBuildRole: CodeBuildRole) {
        super(scope, id);

        const logGroup = new LogGroup(this, 'LogGroup');

        const buildSpec = BuildSpec.fromObject({
            version: '0.2',
            phases: {
                pre_build: {
                    commands: [
                        'aws --version && docker --version && java --version && mvn --version',
                        `aws ecr get-login-password --region ${process.env.CDK_DEFAULT_REGION} | docker login --username AWS --password-stdin ${process.env.CDK_DEFAULT_ACCOUNT}.dkr.ecr.${process.env.CDK_DEFAULT_REGION}.amazonaws.com`,
                    ],
                },
                build: {
                    commands: [
                        'cd app/',
                        `docker build -t ${imageRegistry.repositoryUri} .`,
                        `docker tag ${imageRegistry.repositoryUri}:latest ${imageRegistry.repositoryUri}:$CODEBUILD_RESOLVED_SOURCE_VERSION`,
                        `docker push --all-tags ${imageRegistry.repositoryUri}`,
                        'cd ..',
                    ],
                },
                post_build: {
                    commands: [
                        // Generate imagedefinitions.json for ECS deploy action
                        `printf '[{"name":"${process.env.PROJECT_DEPLOYMENT_NAME}","imageUri":"%s"}]' "${imageRegistry.repositoryUri}:$CODEBUILD_RESOLVED_SOURCE_VERSION" > imagedefinitions.json`,
                    ],
                },
            },
            artifacts: {
                files: ['imagedefinitions.json'],
            },
            reports: {
                SurefireReports: {
                    files: ['**/*'],
                    'base-directory': 'app/target/surefire-reports',
                },
            },
            cache: {
                paths: ['/root/.m2/**/*'],
            },
        });

        this.project = new PipelineProject(this, 'Project', {
            projectName: 'fargate-build-app',
            role: codeBuildRole.role,
            environment: {
                computeType: ComputeType.SMALL,
                buildImage: LinuxBuildImage.STANDARD_6_0,
                privileged: true,
            },
            environmentVariables: {
                PROJECT_POM: { value: 'app/pom.xml' },
            },
            buildSpec,
            cache: Cache.bucket(artifactBucket.bucket, { prefix: 'cache' }),
            logging: { cloudWatch: { logGroup } },
        });
    }
}
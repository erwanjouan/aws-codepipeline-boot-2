import { BuildSpec, Cache, ComputeType, LinuxBuildImage, PipelineProject } from "aws-cdk-lib/aws-codebuild";
import { Construct } from "constructs";
import { LogGroup } from "aws-cdk-lib/aws-logs";
import { ArtifactBucket } from "./artifact-bucket";
import { CodeBuildRole } from "./code-build-role";
import { ImageRegistry } from "./image-registry";

export class CodeBuildApp extends Construct {

    project: PipelineProject

    constructor(scope: Construct, id: string, deploymentName: string, projectDeploymentName: string, artifactBucket: ArtifactBucket,
        imageRegistry: ImageRegistry, codeBuildServiceRole: CodeBuildRole) {
        super(scope, id);

        const cbLogGroup = new LogGroup(this, `/aws/codebuild/${projectDeploymentName}/app`)

        const buildSpec = BuildSpec.fromObject({
            version: '0.2',
            phases: {
                pre_build: {
                    commands: [
                        'aws --version && docker --version && java --version && mvn --version',
                        `aws ecr get-login-password --region ${process.env.CDK_DEFAULT_REGION} | docker login --username AWS --password-stdin ${process.env.CDK_DEFAULT_ACCOUNT}.dkr.ecr.${process.env.CDK_DEFAULT_REGION}.amazonaws.com`
                    ],
                },
                build: {
                    commands: [
                        'mvn package -f $PROJECT_POM',
                    ],
                },
                post_build: {
                    commands: [
                        `cd app/`,
                        `docker build -t ${imageRegistry.repositoryUri} .`,
                        `docker tag ${imageRegistry.repositoryUri}:latest ${imageRegistry.repositoryUri}:$CODEBUILD_RESOLVED_SOURCE_VERSION`,
                        `docker push --all-tags ${imageRegistry.repositoryUri}`,
                    ],
                }
            },
            reports: {
                SurefireReports: {
                    files: ['**/*'],
                    'base-directory': 'app/target/surefire-reports',
                }
            },
            cache: {
                paths: [
                    '/root/.m2/**/*',
                ],
            }
        });

        const codebuildAppProject = new PipelineProject(this, 'CodeBuildApp', {
            projectName: `${deploymentName}-app`,
            role: codeBuildServiceRole.role,
            environment: {
                computeType: ComputeType.SMALL,
                buildImage: LinuxBuildImage.STANDARD_7_0,
                privileged: true
            },
            environmentVariables: {
                PROJECT_POM: {
                    value: 'app/pom.xml'
                }
            },
            buildSpec: buildSpec,
            cache: Cache.bucket(artifactBucket.bucket, {
                prefix: 'cache'
            }),
            logging: {
                cloudWatch: {
                    logGroup: cbLogGroup,
                }
            }
        })
        this.project = codebuildAppProject
    }
}
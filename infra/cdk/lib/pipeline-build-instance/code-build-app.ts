import { BuildSpec, Cache, ComputeType, LinuxBuildImage, PipelineProject } from "aws-cdk-lib/aws-codebuild";
import { Construct } from "constructs";
import { LogGroup } from "aws-cdk-lib/aws-logs";
import { ArtifactBucket } from "./artifact-bucket";
import { CodeArtifact } from "./code-artifact";
import { CodeBuildRole } from "./code-build-role";

export class CodeBuildApp extends Construct {

    project: PipelineProject

    constructor(scope: Construct, id: string, deploymentName: string, projectDeploymentName: string, artifactBucket: ArtifactBucket,
        codeArtifact: CodeArtifact, codeBuildServiceRole: CodeBuildRole) {
        super(scope, id);

        const cbLogGroup = new LogGroup(this, `/aws/codebuild/${projectDeploymentName}/app`)

        const buildSpec = BuildSpec.fromObject({
            version: '0.2',
            phases: {
                pre_build: {
                    commands: [
                        'java --version && mvn --version',
                        `export CODEARTIFACT_AUTH_TOKEN=$(aws codeartifact get-authorization-token --domain ${codeArtifact.domainName} --domain-owner ${process.env.CDK_DEFAULT_ACCOUNT} --query authorizationToken --output text)`
                    ],
                },
                build: {
                    commands: [
                        'mvn deploy -f $PROJECT_POM --settings $MAVEN_SETTINGS_FILE',
                    ],
                },
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
                buildImage: LinuxBuildImage.STANDARD_6_0,
                privileged: true
            },
            environmentVariables: {
                CODEARTIFACT_REPOSITORY_URL: {
                    value: codeArtifact.repositoryUrl
                },
                MAVEN_SETTINGS_FILE: {
                    value: 'infra/cdk/lib/pipeline-build-instance/settings.xml'
                },
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
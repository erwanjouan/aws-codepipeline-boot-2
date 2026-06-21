import { BuildSpec, Cache, ComputeType, LinuxBuildImage, PipelineProject } from 'aws-cdk-lib/aws-codebuild';
import { LogGroup } from 'aws-cdk-lib/aws-logs';
import { Construct } from 'constructs';
import { ArtifactBucket } from './artifact-bucket';
import { CodeArtifactRepo } from './code-artifact-repo';
import { CodeBuildRole } from './code-build-role';

export class CodeBuildApp extends Construct {
    project: PipelineProject;

    constructor(scope: Construct, id: string, artifactBucket: ArtifactBucket, codeArtifactRepo: CodeArtifactRepo, codeBuildRole: CodeBuildRole) {
        super(scope, id);

        const logGroup = new LogGroup(this, 'LogGroup');
        const domain = codeArtifactRepo.domainName;
        const repository = codeArtifactRepo.repositoryName;

        const buildSpec = BuildSpec.fromObject({
            version: '0.2',
            phases: {
                install: {
                    'runtime-versions': {
                        java: 'corretto21',
                    },
                },
                pre_build: {
                    commands: [
                        'aws --version && java --version && mvn --version',
                        `export CODEARTIFACT_AUTH_TOKEN=$(aws codeartifact get-authorization-token --domain ${domain} --query authorizationToken --output text)`,
                        `export CODEARTIFACT_REPOSITORY_URL=$(aws codeartifact get-repository-endpoint --domain ${domain} --repository ${repository} --format maven --query repositoryEndpoint --output text)`,
                        // Substitute env vars into the Maven settings template
                        'envsubst < infra/cdk/lib/asg-rolling-update/.m2/settings.xml > /tmp/settings.xml',
                    ],
                },
                build: {
                    commands: [
                        'mvn deploy -f app/pom.xml -s /tmp/settings.xml --no-transfer-progress -PpublishingCodeArtifact -DaltDeploymentRepository=codeartifact::default::${CODEARTIFACT_REPOSITORY_URL}',
                    ],
                },
                post_build: {
                    commands: [
                        `echo '{"domain":"${domain}","repository":"${repository}"}' > build-info.json`,
                    ],
                },
            },
            artifacts: {
                files: ['build-info.json'],
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
            projectName: 'asg-rolling-build-app',
            role: codeBuildRole.role,
            environment: {
                computeType: ComputeType.SMALL,
                buildImage: LinuxBuildImage.STANDARD_7_0,
            },
            buildSpec,
            cache: Cache.bucket(artifactBucket.bucket, { prefix: 'cache' }),
            logging: { cloudWatch: { logGroup } },
        });
    }
}

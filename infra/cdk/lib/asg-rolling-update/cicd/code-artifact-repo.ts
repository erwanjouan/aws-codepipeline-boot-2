import { RemovalPolicy } from 'aws-cdk-lib';
import { AccountPrincipal, Effect, PolicyDocument, PolicyStatement } from 'aws-cdk-lib/aws-iam';
import { CfnDomain, CfnRepository } from 'aws-cdk-lib/aws-codeartifact';
import { Construct } from 'constructs';
import { Constants } from '../../constants';

export class CodeArtifactRepo extends Construct {
    readonly domainName: string;
    readonly repositoryName: string;

    constructor(scope: Construct, id: string) {
        super(scope, id);

        this.domainName = process.env.PROJECT_NAME!;
        this.repositoryName = process.env.DEPLOYMENT_NAME!;

        const domain = new CfnDomain(this, 'Domain', {
            domainName: this.domainName,
            // Allow PROD account to get authorization tokens and pull packages
            permissionsPolicyDocument: new PolicyDocument({
                statements: [
                    new PolicyStatement({
                        effect: Effect.ALLOW,
                        principals: [new AccountPrincipal(Constants.WORKLOAD_ACCOUNT_ID)],
                        actions: [
                            'codeartifact:GetAuthorizationToken',
                            'codeartifact:GetRepositoryEndpoint',
                            'codeartifact:ReadFromRepository',
                        ],
                        resources: ['*'],
                    }),
                ],
            }).toJSON(),
        });

        new CfnRepository(this, 'Repository', {
            domainName: this.domainName,
            repositoryName: this.repositoryName,
            externalConnections: ['public:maven-central'],
        }).addDependency(domain);
    }
}

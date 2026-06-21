import { Effect, ManagedPolicy, PolicyStatement, Role, ServicePrincipal } from 'aws-cdk-lib/aws-iam';
import { Construct } from 'constructs';

export class Ec2Role extends Construct {
    readonly role: Role;

    constructor(scope: Construct, id: string) {
        super(scope, id);

        this.role = new Role(this, 'Role', {
            assumedBy: new ServicePrincipal('ec2.amazonaws.com'),
            managedPolicies: [
                ManagedPolicy.fromAwsManagedPolicyName('AmazonSSMManagedInstanceCore'),
                ManagedPolicy.fromAwsManagedPolicyName('AmazonS3FullAccess'),
                ManagedPolicy.fromAwsManagedPolicyName('AmazonEC2ReadOnlyAccess'),
                ManagedPolicy.fromAwsManagedPolicyName('CloudWatchAgentServerPolicy'),
            ],
        });

        this.role.addToPolicy(new PolicyStatement({
            effect: Effect.ALLOW,
            actions: [
                'codeartifact:GetAuthorizationToken',
                'codeartifact:GetRepositoryEndpoint',
                'codeartifact:ReadFromRepository',
                'codeartifact:ListPackageVersionAssets',
                'codeartifact:GetPackageVersionAsset',
            ],
            resources: ['*'],
        }));

        this.role.addToPolicy(new PolicyStatement({
            effect: Effect.ALLOW,
            actions: ['sts:GetServiceBearerToken'],
            resources: ['*'],
            conditions: {
                StringEquals: { 'sts:AWSServiceName': 'codeartifact.amazonaws.com' },
            },
        }));
    }
}

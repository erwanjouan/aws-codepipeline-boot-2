import { CfnOutput } from "aws-cdk-lib";
import { Effect, InstanceProfile, ManagedPolicy, PolicyStatement, Role, ServicePrincipal } from "aws-cdk-lib/aws-iam";
import { Construct } from "constructs";
  
export class Ec2InstanceProfile extends Construct {

    constructor(scope: Construct, id: string) {
        super(scope, id);

        const policy = new ManagedPolicy(this, "MyManagedPolicy", {
            statements: [
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: [
                        "ec2:RunInstances",
                        "ec2:CreateTags",
                        "iam:PassRole",
                        "logs:PutRetentionPolicy"
                    ],
                    resources: ["*"]
                })
            ]
        }
        )

        const role = new Role(this, 'myRole', {
            assumedBy: new ServicePrincipal('ec2.amazonaws.com'),
            roleName: "ec2-image-builder-role",
            managedPolicies: [
                ManagedPolicy.fromAwsManagedPolicyName("AWSCodeDeployFullAccess"),
                ManagedPolicy.fromAwsManagedPolicyName("AmazonSSMManagedInstanceCore"),
                ManagedPolicy.fromAwsManagedPolicyName("AmazonS3FullAccess"),
                ManagedPolicy.fromAwsManagedPolicyName("AmazonEC2ReadOnlyAccess"),
                ManagedPolicy.fromAwsManagedPolicyName("CloudWatchAgentServerPolicy"),
                ManagedPolicy.fromAwsManagedPolicyName("AWSImageBuilderFullAccess"),
                policy]
        })

        const instanceProfile = new InstanceProfile(this, 'instanceProfile', {
            role: role
        })

        this.name = instanceProfile.instanceProfileName
    }
    name: string;
}
import { Port, SecurityGroup, Vpc } from 'aws-cdk-lib/aws-ec2';
import { Construct } from 'constructs';

export class WebappSg extends Construct {
    readonly securityGroup: SecurityGroup;

    constructor(scope: Construct, id: string, vpc: Vpc, albSg: SecurityGroup) {
        super(scope, id);

        this.securityGroup = new SecurityGroup(this, 'Sg', {
            vpc,
            description: 'EC2: allow inbound from ALB on application port',
        });
        this.securityGroup.addIngressRule(albSg, Port.tcp(8080), 'Allow from ALB');
    }
}

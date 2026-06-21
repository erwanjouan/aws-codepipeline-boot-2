import { Construct } from 'constructs';
import { SubnetType, Vpc as Ec2Vpc } from 'aws-cdk-lib/aws-ec2';

export class Vpc extends Construct {
    readonly vpc: Ec2Vpc;

    constructor(scope: Construct, id: string) {
        super(scope, id);

        this.vpc = new Ec2Vpc(this, 'Vpc', {
            maxAzs: 2,
            natGateways: 1,
            subnetConfiguration: [
                { name: 'public', subnetType: SubnetType.PUBLIC, cidrMask: 24 },
                { name: 'private', subnetType: SubnetType.PRIVATE_WITH_EGRESS, cidrMask: 24 },
            ],
        });
    }
}

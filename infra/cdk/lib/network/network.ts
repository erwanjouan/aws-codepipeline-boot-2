import { DefaultInstanceTenancy, IpAddresses, SubnetType, Vpc } from "aws-cdk-lib/aws-ec2";
import { Construct } from "constructs";

export class Network extends Construct {
    
    constructor(scope: Construct, id: string) {
        
        super(scope, id);

        const vpc = new Vpc(this, 'vpc', {
            ipAddresses: IpAddresses.cidr('10.0.0.0/16'),
            maxAzs: 2,
            enableDnsHostnames: true,
            enableDnsSupport: true,
            defaultInstanceTenancy: DefaultInstanceTenancy.DEFAULT,
            subnetConfiguration: [
                {
                    cidrMask: 24,
                    name: 'private',
                    subnetType: SubnetType.PRIVATE_WITH_EGRESS,
                },
                {
                    cidrMask: 24,
                    name: 'public',
                    subnetType: SubnetType.PUBLIC,
                }
            ]
        });
    }
}
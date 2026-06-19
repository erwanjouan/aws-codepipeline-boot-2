import { CfnDistributionConfiguration } from "aws-cdk-lib/aws-imagebuilder";
import { Construct } from "constructs";
import { Constants } from "../constants";
import { Ec2Architecture } from "../utils/ec2-architecture";

export class DistributionConfiguration extends Construct {

    constructor(scope: Construct, id: string, architecture:Ec2Architecture) {
        super(scope, id)

        const organizationalUnitArn = `arn:aws:organizations::${Constants.DEFAULT_ACCOUNT}:ou/${Constants.ORGANIZATION_ID}/${Constants.ORGANIZATION_UNIT_ID}`;

        const lcProperty: CfnDistributionConfiguration.LaunchPermissionConfigurationProperty = {
            organizationalUnitArns: [organizationalUnitArn]
        }

        const amiDistributionConfigurationName = `${Constants.PROJECT_NAME}-${architecture.label}-${Constants.DEFAULT_REGION}-{{ imagebuilder:buildDate }}`

        const amiDistributionConfiguration: CfnDistributionConfiguration.AmiDistributionConfigurationProperty = {
            name: amiDistributionConfigurationName,
            amiTags: {
                "Name": Constants.PROJECT_NAME,
                "Architecture": architecture.label,
                "BaseOs": "al2023"
            },
            description: "Ami with agents and Java",
            launchPermissionConfiguration: lcProperty
        }

        const distProps: CfnDistributionConfiguration.DistributionProperty = {
            region: Constants.DEFAULT_REGION,
            amiDistributionConfiguration: amiDistributionConfiguration
        }

        const distributionConfiguration = new CfnDistributionConfiguration(this, 'distributionConfiguration', {
            name: Constants.PROJECT_NAME,
            distributions: [distProps],
            tags: {
                "Name": Constants.PROJECT_NAME,
                "Architecture": architecture.label,
                "BaseOs": "al2023"
            }
        })
        this.arn = distributionConfiguration.attrArn
    }
    arn: string;
}
import { Construct } from "constructs";
import * as ssm from 'aws-cdk-lib/aws-ssm';
import { CfnImageRecipe } from "aws-cdk-lib/aws-imagebuilder";
import { Constants } from "../constants";
import { Ec2Architecture } from "../utils/ec2-architecture";

export class ImageRecipe extends Construct {

    constructor(scope: Construct, id: string, binaryComponentArn: string, configComponentArn: string, architecture:Ec2Architecture) {
        super(scope, id)

        const parameterStoreName = `/aws/service/ami-amazon-linux-latest/al2023-ami-kernel-6.1-${architecture.label}`
        const parentImage = ssm.StringParameter.valueForStringParameter(this, parameterStoreName);

        const cfnImageRecipe = new CfnImageRecipe(this, 'imageRecipe', {
            name: Constants.PROJECT_NAME,
            parentImage: parentImage,
            version: "1.0.0",
            components: [
                { componentArn: binaryComponentArn },
                { componentArn: 'arn:aws:imagebuilder:eu-west-1:aws:component/aws-codedeploy-agent-linux/x.x.x' },
                { componentArn: 'arn:aws:imagebuilder:eu-west-1:aws:component/amazon-cloudwatch-agent-linux/x.x.x' },
                { componentArn: configComponentArn }
            ]
        })
        this.arn = cfnImageRecipe.attrArn
    }
    arn: string;

}
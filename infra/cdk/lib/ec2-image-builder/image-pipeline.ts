import { CfnImagePipeline } from "aws-cdk-lib/aws-imagebuilder";
import { Construct } from "constructs";
import { Constants } from "../constants";

export class ImagePipeline extends Construct {
    constructor(scope: Construct, id: string, distributionConfigurationArn: string, imageRecipeArn: string,
        infrastructureConfigurationArn: string) {

        super(scope, id)

        const imagePipeline = new CfnImagePipeline(this, 'imagePipeline', {
            name: `${Constants.PROJECT_NAME}-pipeline`,
            description: "Pipeline for EC2 Image Builder",
            distributionConfigurationArn: distributionConfigurationArn,
            imageRecipeArn: imageRecipeArn,
            infrastructureConfigurationArn: infrastructureConfigurationArn,
            status: "ENABLED",
            tags: {
                'Name': Constants.PROJECT_NAME
            }
        })
    }
}
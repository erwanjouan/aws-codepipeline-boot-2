import { CfnImagePipeline } from "aws-cdk-lib/aws-imagebuilder";
import { Construct } from "constructs";
import { Constants } from "../constants";

export class ImagePipeline extends Construct {
    constructor(scope: Construct, id: string, distributionConfigurationArn: string, imageRecipeArn: string,
        infrastructureConfigurationArn: string) {

        super(scope, id);

        new CfnImagePipeline(this, 'imagePipeline', {
            name: `${process.env.PROJECT_NAME}-pipeline`,
            description: "Pipeline for EC2 Image Builder",
            distributionConfigurationArn: distributionConfigurationArn,
            imageRecipeArn: imageRecipeArn,
            infrastructureConfigurationArn: infrastructureConfigurationArn,
            status: "ENABLED",
            schedule: {
                scheduleExpression: 'cron(0 0 ? * SUN *)',
                pipelineExecutionStartCondition: 'EXPRESSION_MATCH_ONLY'
            },
            tags: {
                'Name': `${process.env.PROJECT_NAME}`
            }
        });
    }
}

import { Repository } from "aws-cdk-lib/aws-ecr";
import { Construct } from "constructs";
import { Constants } from "../constants";
import { RemovalPolicy } from "aws-cdk-lib";

export class ImageRegistry extends Construct {
    
    repositoryUri: string

    constructor(scope: Construct, id: string, deploymentName: string) {
        super(scope, id)

        const registry = new Repository(this, 'image-registry', {
            repositoryName: `${Constants.PROJECT_NAME}-${deploymentName}`,
            removalPolicy: RemovalPolicy.DESTROY,
            emptyOnDelete: true
        })

        this.repositoryUri = registry.repositoryUri
    }
}
import { PolicyStatement } from "aws-cdk-lib/aws-iam";
import { Bucket } from "aws-cdk-lib/aws-s3";
import { Construct } from "constructs";
import { CodePipelineRole } from "./code-pipeline-role";
import { RemovalPolicy } from "aws-cdk-lib";

export class ArtifactBucket extends Construct {

    bucket: Bucket

    constructor(scope: Construct, id: string, codePipelineRole: CodePipelineRole) {
        super(scope, id)

        const myBucket = new Bucket(this, 'mySSEKMSEncryptedBucket', {
            removalPolicy: RemovalPolicy.DESTROY,
            autoDeleteObjects: true,
        });

        const bucketPolicy = new PolicyStatement({
            actions: ['s3:*'],
            principals: [
                codePipelineRole.principal
            ],
            resources: [myBucket.bucketArn, myBucket.arnForObjects('*')],
        });

        this.bucket = myBucket

    }
}
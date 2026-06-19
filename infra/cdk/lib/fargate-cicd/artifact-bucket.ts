import { Bucket } from 'aws-cdk-lib/aws-s3';
import { AccountPrincipal, Effect, PolicyStatement } from 'aws-cdk-lib/aws-iam';
import { RemovalPolicy } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { Constants } from '../constants';
import { ArtifactKmsKey } from './artifact-kms-key';

export class ArtifactBucket extends Construct {
    bucket: Bucket;

    constructor(scope: Construct, id: string, kmsKey: ArtifactKmsKey) {
        super(scope, id);

        this.bucket = new Bucket(this, 'Bucket', {
            removalPolicy: RemovalPolicy.DESTROY,
            autoDeleteObjects: true,
            encryptionKey: kmsKey.key,
            enforceSSL: true,
        });

        // Allow PROD account cross-account role to read pipeline artifacts
        this.bucket.addToResourcePolicy(new PolicyStatement({
            effect: Effect.ALLOW,
            principals: [new AccountPrincipal(Constants.WORKLOAD_ACCOUNT_ID)],
            actions: ['s3:Get*', 's3:List*'],
            resources: [this.bucket.bucketArn, `${this.bucket.bucketArn}/*`],
        }));
    }
}
import { Construct } from 'constructs';

export class GithubSource extends Construct {
    readonly owner = 'erwanjouan';
    readonly repo = 'aws-codepipeline-boot-2';
    readonly branch = 'main';
    readonly connectionArn: string;

    constructor(scope: Construct, id: string) {
        super(scope, id);
        this.connectionArn = process.env.AWS_GITHUB_CONNECTION_ARN!;
    }
}

import { IRepository, Repository } from 'aws-cdk-lib/aws-codecommit';
import { Construct } from 'constructs';

export class CodeCommit extends Construct {
    repo: IRepository;

    constructor(scope: Construct, id: string) {
        super(scope, id);
        this.repo = Repository.fromRepositoryName(this, 'Repo', 'aws-codepipeline-boot');
    }
}
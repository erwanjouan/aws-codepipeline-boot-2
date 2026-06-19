import { IRepository, Repository } from "aws-cdk-lib/aws-codecommit";
import { Construct } from "constructs";

export class CodeCommit extends Construct {
    repo: IRepository
    constructor(scope: Construct, id: string) {
        super(scope, id);
        const codeCommitRepo = Repository.fromRepositoryName(this, 'CodeCommitRepo', 'aws-codepipeline-boot')
        this.repo = codeCommitRepo
    }

}
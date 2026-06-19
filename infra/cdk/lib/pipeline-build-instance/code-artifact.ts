import { CfnDomain, CfnRepository } from "aws-cdk-lib/aws-codeartifact";
import { ArnPrincipal, Effect, PolicyDocument, PolicyStatement } from "aws-cdk-lib/aws-iam";
import { Construct } from "constructs";
import { Constants } from "../constants";

export class CodeArtifact extends Construct {
    constructor(scope: Construct, id: string, deploymentName:string) {
        super(scope, id);

        const arnPrincipal = new ArnPrincipal(`arn:aws:iam::${Constants.DEFAULT_ACCOUNT}:root`);

        const artifactDomain = new CfnDomain(this, 'CodeArtifactDomain', {
            domainName: deploymentName,
            permissionsPolicyDocument: new PolicyDocument({
                statements: [
                    new PolicyStatement({
                        principals: [arnPrincipal],
                        effect: Effect.ALLOW,
                        actions: ["codeartifact:GetAuthorizationToken"],
                        resources: ["*"]
                    })
                ]
            })
        })

        const artifactRepository = new CfnRepository(this, 'CodeArtifactRepository', {
            domainName: artifactDomain.domainName,
            domainOwner: process.env.CDK_DEFAULT_ACCOUNT,
            repositoryName: deploymentName,
            // allows to mirror maven-central
            externalConnections: ["public:maven-central"],
            permissionsPolicyDocument: new PolicyDocument({
                statements: [
                    new PolicyStatement({
                        principals: [new ArnPrincipal(`arn:aws:iam::${process.env.CDK_DEFAULT_ACCOUNT}:root`)],
                        effect: Effect.ALLOW,
                        actions: [
                            "codeartifact:DescribePackageVersion",
                            "codeartifact:DescribeRepository",
                            "codeartifact:GetPackageVersionReadme",
                            "codeartifact:GetRepositoryEndpoint",
                            "codeartifact:GetPackageVersionAsset",
                            "codeartifact:ListPackages",
                            "codeartifact:ListPackageVersions",
                            "codeartifact:ListPackageVersionAssets",
                            "codeartifact:ListPackageVersionDependencies",
                            "codeartifact:ReadFromRepository"
                        ],
                        resources: ["*"]
                    })
                ]
            })
        })

        artifactRepository.node.addDependency(artifactDomain)

        this.domainName = artifactDomain.domainName
        this.repositoryName = artifactRepository.repositoryName    
        this.repositoryUrl = `https://${this.domainName}-${process.env.CDK_DEFAULT_ACCOUNT}.d.codeartifact.${process.env.CDK_DEFAULT_REGION}.amazonaws.com/maven/${this.repositoryName}/`
    }

    domainName:string
    repositoryName:string
    repositoryUrl:string
}
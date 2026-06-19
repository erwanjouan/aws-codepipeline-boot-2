# fargate-cicd

CDK stack for the CICD account (`163822821219`). Deploys a CodePipeline that sources from GitHub, builds with CodeBuild, and deploys to the Fargate service in the PROD account.

## Manual prerequisites

### GitHub CodeStar Connection

The pipeline sources code from GitHub via a CodeStar Connection. This connection requires a one-time manual authorization through the AWS console — it cannot be created fully by CDK or CLI alone.

1. In the **CICD account** (`163822821219`), go to **Developer Tools → Settings → Connections**
2. Click **Create connection**, select **GitHub**
3. Name it (e.g. `github-aws-codepipeline-boot-2`) and click **Connect to GitHub**
4. Authorize the AWS Connector for GitHub app
5. Once the connection status is **Available**, copy the connection ARN
6. Store it as a GitHub Actions secret named `AWS_GITHUB_CONNECTION_ARN`

The ARN format is:
```
arn:aws:codestar-connections:eu-west-1:163822821219:connection/<uuid>
```

export abstract class Constants {
    static readonly DEFAULT_REGION = process.env.AWS_REGION!
    static readonly DEFAULT_ACCOUNT = process.env.CICD_ACCOUNT_ID!
    static readonly WORKLOAD_ACCOUNT_ID = process.env.PROD_ACCOUNT_ID!
    static readonly ORGANIZATION_ID = process.env.AWS_ORGANIZATION_ID!
    static readonly ORGANIZATION_UNIT_ID = process.env.AWS_ORGANIZATION_UNIT_ID!
    static readonly PARAMETER_STORE_AMI = "/custom/ami/al2023"
    static readonly ARTIFACT_NAME = "app.jar"
    static readonly FARGATE_CROSS_ACCOUNT_ROLE_NAME = 'fargate-cross-account-deploy-role'
    static readonly ASG_CROSS_ACCOUNT_ROLE_NAME = 'asg-rolling-cross-account-deploy-role'
  }

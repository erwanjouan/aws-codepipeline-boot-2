import { AsgRollingUpdateStack } from "../asg-rolling-update/CdkStack";
import { Construct } from "constructs";
import { Stage, StageProps, Stack, StackProps, Aws } from "aws-cdk-lib";

export class AppStage extends Stage {
    public readonly apiStack: AsgRollingUpdateStack;
  
    constructor(scope: Construct, id: string, deploymentName:string, props?: StageProps) {
      super(scope, id, props);
      
      const asgRollingUpdateStack = new AsgRollingUpdateStack(this, "ddb-stack", {
        stackName: `${deploymentName}-app`,
        deploymentName: deploymentName
      })
    }
  }
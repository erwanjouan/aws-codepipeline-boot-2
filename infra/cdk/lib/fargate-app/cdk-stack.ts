import { Stack } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { CustomStackProps } from '../utils/custom-stack-props';
import { TaskExecutionRole } from './task-execution-role';
import { CrossAccountDeployRole } from './cross-account-deploy-role';
import { EcsFargateService } from './ecs-fargate-service';

export class FargateAppStack extends Stack {
    constructor(scope: Construct, id: string, props?: CustomStackProps) {
        super(scope, id, props);

        const taskExecutionRole = new TaskExecutionRole(this, 'taskExecutionRole');
        new CrossAccountDeployRole(this, 'crossAccountDeployRole', taskExecutionRole);
        new EcsFargateService(this, 'ecsFargateService', taskExecutionRole);
    }
}

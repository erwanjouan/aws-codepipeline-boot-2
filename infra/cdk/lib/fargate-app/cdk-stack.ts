import { Stack } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { CustomStackProps } from '../utils/custom-stack-props';
import { TaskExecutionRole } from './task-execution-role';
import { TaskRole } from './task-role';
import { CrossAccountDeployRole } from './cross-account-deploy-role';
import { EcsFargateService } from './ecs-fargate-service';
import { EcsAutoscaling } from './ecs-autoscaling';
import { StressParameter } from './stress-parameter';

export class FargateAppStack extends Stack {
    constructor(scope: Construct, id: string, props?: CustomStackProps) {
        super(scope, id, props);

        const taskExecutionRole = new TaskExecutionRole(this, 'taskExecutionRole');
        const taskRole = new TaskRole(this, 'taskRole');
        new CrossAccountDeployRole(this, 'crossAccountDeployRole', taskExecutionRole);
        const ecsFargateService = new EcsFargateService(this, 'ecsFargateService', taskExecutionRole, taskRole);
        new EcsAutoscaling(this, 'ecsAutoscaling', ecsFargateService.service);
        new StressParameter(this, 'stressParameter');
    }
}

import { Duration } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { FargateService } from 'aws-cdk-lib/aws-ecs';
import { AdjustmentType, MetricAggregationType } from 'aws-cdk-lib/aws-applicationautoscaling';

export class EcsAutoscaling extends Construct {
    constructor(scope: Construct, id: string, service: FargateService) {
        super(scope, id);

        const scaling = service.autoScaleTaskCount({
            minCapacity: 2,
            maxCapacity: 10,
        });

        // Scale down when CPU <= 20%; scale up with step adjustments above 70%:
        //   70–85% → +1 task, 85–95% → +2 tasks, 95%+ → +3 tasks
        scaling.scaleOnMetric('CpuScaling', {
            metric: service.metricCpuUtilization({
                period: Duration.seconds(60),
                statistic: 'Average',
            }),
            scalingSteps: [
                { upper: 20, change: -1 },
                { lower: 70, change: +1 },
                { lower: 85, change: +2 },
                { lower: 95, change: +3 },
            ],
            adjustmentType: AdjustmentType.CHANGE_IN_CAPACITY,
            metricAggregationType: MetricAggregationType.AVERAGE,
            cooldown: Duration.seconds(60),
            evaluationPeriods: 1,
            datapointsToAlarm: 1,
        });
    }
}

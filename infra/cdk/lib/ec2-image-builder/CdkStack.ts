import * as cdk from 'aws-cdk-lib';
import * as ssm from 'aws-cdk-lib/aws-ssm';
import { Construct } from 'constructs';
import { Ec2InstanceProfile } from './ec2-instance-profile';
import { InfrastructureConfiguration } from './infrastructure-configuration';
import { BinariesComponent } from './binaries-component';
import { ImageRecipe } from './image-recipe';
import { ConfigComponent } from './config-component';
import { DistributionConfiguration } from './distribution-configuration';
import { ImagePipeline } from './image-pipeline';
import { Ec2Image } from './ec2-Image';
import { ParameterStoreUpdater } from './parameter-store-updater';
import { Ec2Architecture } from '../utils/ec2-architecture';

export class CdkStack extends cdk.Stack {

  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const architecture = Ec2Architecture.X86_64

    const instanceProfile = new Ec2InstanceProfile(this, 'instanceProfile')

    const infrastructureConfiguration = new InfrastructureConfiguration(this, 'infrastructureConfiguration', instanceProfile.name, architecture)

    infrastructureConfiguration.bucket.grantWrite(instanceProfile.role)

    const cloudwatchAgentConfig = new ssm.StringParameter(this, 'CloudWatchAgentConfig', {
      parameterName: '/custom/cloudwatch-agent/config/linux',
      description: 'CloudWatch agent configuration for EC2 instances built by Image Builder',
      stringValue: JSON.stringify({
        agent: { metrics_collection_interval: 60 },
        metrics: {
          append_dimensions: {
            AutoScalingGroupName: '${aws:AutoScalingGroupName}',
            ImageId: '${aws:ImageId}',
            InstanceId: '${aws:InstanceId}',
            InstanceType: '${aws:InstanceType}'
          },
          metrics_collected: {
            cpu: {
              measurement: ['cpu_usage_idle', 'cpu_usage_iowait', 'cpu_usage_user', 'cpu_usage_system'],
              metrics_collection_interval: 60,
              totalcpu: false
            },
            disk: {
              measurement: ['used_percent', 'inodes_free'],
              metrics_collection_interval: 60,
              resources: ['*']
            },
            mem: {
              measurement: ['mem_used_percent'],
              metrics_collection_interval: 60
            }
          }
        }
      })
    });
    cloudwatchAgentConfig.grantRead(instanceProfile.role)

    const binariesComponent = new BinariesComponent(this, 'binariesComponent')

    const configComponent = new ConfigComponent(this, 'configComponent')

    const imageRecipe = new ImageRecipe(this, 'ImageRecipe', binariesComponent.arn, configComponent.arn, architecture)

    const distributionConfiguration = new DistributionConfiguration(this, 'DistributionConfiguration', architecture)

    const imagePipeline = new ImagePipeline(this, 'ImagePipeline', distributionConfiguration.arn, imageRecipe.arn, infrastructureConfiguration.arn)

    const ec2Image = new Ec2Image(this, 'ec2Image', imageRecipe.arn, distributionConfiguration.arn, infrastructureConfiguration.arn)
    ec2Image.node.addDependency(cloudwatchAgentConfig)

    const resource = new ParameterStoreUpdater(this, 'ParameterStoreUpdater', ec2Image.amiId, architecture);

    // Publish the custom resource output
    new cdk.CfnOutput(this, 'ResponseMessage', {
      description: 'The message that came back from the Custom Resource',
      value: resource.response
    });
  }
}

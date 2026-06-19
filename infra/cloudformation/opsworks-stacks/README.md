# Opsworks stacks

CodePipeline deploying on opsworks stacks

## Principle

[Opsworks Stacks](https://docs.aws.amazon.com/opsworks/latest/userguide/welcome_classic.html) is based on a hierarchy of
objects

- [Stack](https://docs.aws.amazon.com/opsworks/latest/userguide/workingstacks.html) : It represents a set of instances
  that you want to manage collectively, typically because they have a common purpose. Stacks can be considered as envs.
  It can be linked to custom cookbook.
    - [1..n Layers](https://docs.aws.amazon.com/opsworks/latest/userguide/workinglayers.html) : represent a stack
      component, such as a load balancer or a set of application servers.
      A [typical use case](https://docs.aws.amazon.com/whitepapers/latest/overview-deployment-options/aws-opsworks.html)
      for Layers is a 3 tier architecture (FrontEnd, Backend, Database), where each tier is handled by a dedicated
      layer. An additional layer can be added for Load Balancing.
        - [1..n Instances](https://docs.aws.amazon.com/opsworks/latest/userguide/workinginstances.html) : An
          instance represents a computing resource, such as an Amazon EC2 instance, which handles the work of serving
          applications, balancing traffic, and so on. Amazon EC2 instances can optionally be a member of multiple
          layers. In that case, AWS OpsWorks Stacks runs the recipes to install and configure packages, deploy
          applications, and so on for each of the instance's layers.
        - [1..n Apps](https://docs.aws.amazon.com/opsworks/latest/userguide/workingapps.html): An AWS OpsWorks
          Stacks app represents code that you want to run on an application server. The code itself resides in a
          repository such as an Amazon S3 archive; the app contains the information required to deploy the code to the
          appropriate application server instances.
            - [Deployment](https://docs.aws.amazon.com/opsworks/latest/userguide/workingapps-deploying.html) When you
              deploy an application, AWS OpsWorks Stacks triggers a __Deploy event__, which runs each layer's __Deploy__
              recipes. AWS OpsWorks Stacks also installs stack configuration and deployment attributes that contain all
              of the information needed to deploy the app, such as the app's repository and database connection data.

## Chef

### Cookbook

A custom cookbook (~Playbook in Ansible) can be associated with an OpsWorks Stack. It is a collection of directives
grouped in Recipes.

### Lifecycle events

Recipes from custom cookbook can be mapped
to [LifeCycle events of Opsworks](https://docs.aws.amazon.com/opsworks/latest/userguide/workingcookbook-events.html):

- Setup
- Configure
- Deploy
- Undeploy
- Shutdown

Each layer has a set of five lifecycle events, each of which has an associated set of recipes that are specific to the
layer. When an event occurs on a layer's instance, AWS OpsWorks Stacks automatically runs the appropriate set of
recipes. To provide a custom response to these events, implement custom recipes and assign them to the appropriate
events for each layer. AWS OpsWorks Stacks runs those recipes after the event's built-in recipes.

### Deployment

Deployments in Opswork are not triggered automatically when updating "Aplication Source" in Apps section.
But the following modes can be executed via a sequence of manual actions :

- AllAtOnce : Trigger the deploy button in the Management Console of "Apps" section
- Rolling : Unregister an instance from ELB, update it and re-register again
- Blue Green Deployment : Clone the stack and point route53 alias to the updated stack
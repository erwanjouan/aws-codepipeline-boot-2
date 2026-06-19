#!/usr/bin/env python
import boto3

ecr = boto3.client('ecr')
containerImageRegistry = "aws-codepipeline-boot-alb-ecs-fargate"

image_ids = ecr.list_images(repositoryName=containerImageRegistry)['imageIds']
response = ecr.batch_delete_image(repositoryName=containerImageRegistry, imageIds=image_ids)

print(response)

#!/usr/bin/env python
import boto3

s3 = boto3.resource('s3')
bucket = s3.Bucket('aws-codepipeline-boot-alb-ecs-fargate')
bucket.objects.all().delete()
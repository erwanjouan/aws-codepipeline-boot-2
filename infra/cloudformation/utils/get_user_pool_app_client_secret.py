#!/usr/bin/env python
import boto3

user_pool_id = "eu-west-1_uozNm93mE"
client_id = "2abeub6mlpvnlo9vnapvibcqaj"

client = boto3.client('cognito-idp')
response = client.describe_user_pool_client(
    UserPoolId=user_pool_id,
    ClientId=client_id
)

print(response['UserPoolClient']['ClientSecret'])
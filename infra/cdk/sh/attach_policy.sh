#!/bin/sh
aws iam create-policy --profile cicd --policy-name "allow-assume-prod-account-role" --policy-document file://policy.json --query "Policy.Arn" --output text
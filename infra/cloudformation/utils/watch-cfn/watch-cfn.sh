#!/bin/bash

stack_status(){
  aws cloudformation describe-stacks \
    --stack-name $1 \
    --query 'Stacks[0].StackStatus' \
    --output text || echo "UNKNOWN"
}

describe_stacks() {
  aws cloudformation describe-stack-resources \
    --stack-name $1 \
    --query 'StackResources[?contains(ResourceStatus,`PROGRESS`)].{ResourceId:LogicalResourceId,Status:ResourceStatus}' \
    --output text || true
}

clear

STACK_STATUS=$(stack_status $1)

while [[ $STACK_STATUS =~ "PROGRESS" ]]; do
    STACK_RESOURCES_STATUS=$(describe_stacks $1)
    clear
    echo $1 "[" $STACK_STATUS "]"
    echo "${STACK_RESOURCES_STATUS}"
    sleep 1
    STACK_STATUS=$(stack_status $1)
done

clear
#!/bin/bash
export RUN_ID=1234567890
export BUCKET="aws-codepipeline-boot-alb-ecs-fargate"

destinationS3(){
  local TASK_ID=$(curl -s "${ECS_CONTAINER_METADATA_URI_V4}/task" | jq -r ".TaskARN" | cut -d "/" -f 3)
  echo "s3://${BUCKET}/${RUN_ID}/${TASK_ID}"
}

loopJfrUpload(){
  local destination=${1}
  while true
  do
    echo ${destination}
    aws s3 sync /tmp/jfr/ ${destination} || true \
    && sleep 30
  done
}

loopJfrUpload $(destinationS3)


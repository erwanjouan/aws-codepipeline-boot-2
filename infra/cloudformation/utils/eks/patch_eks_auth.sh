#!/bin/bash
ACCOUNT_ID=$(aws sts get-caller-identity --query "Account" --output text) && \
CLI_USER_ARN=$(aws sts get-caller-identity --query "Arn" --output text) && \
CLI_USER_NAME=$(echo $CLI_USER_ARN | cut -d / -f 2) && \
MGT_CONSOLE_USER_NAME=devops && \
MAP_USERS="- userarn: arn:aws:iam::$ACCOUNT_ID:user/$MGT_CONSOLE_USER_NAME\n  username: $MGT_CONSOLE_USER_NAME\n  groups:\n    - system:masters\n" && \
MAP_USERS="$MAP_USERS- userarn: $CLI_USER_ARN\n  username: $CLI_USER_NAME\n  groups:\n    - system:masters" && \
TMP_FILE=/tmp/aws-auth_configmap.json && \
kubectl get cm aws-auth -n kube-system -o json > $TMP_FILE && \
jq --arg add "$MAP_USERS" '.data.mapUsers = $add' $TMP_FILE | sed -e 's/\\\\n/\\n/g' | kubectl apply -f -
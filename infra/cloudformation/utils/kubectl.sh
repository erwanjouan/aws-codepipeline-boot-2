#/bin/sh
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
TARGET_ROLE="arn:aws:iam::${AWS_ACCOUNT_ID}:role/CloudformationRole"
ROLE_SESSION_NAME=EksClusterAccess
ASSUME_ROLE_CREDS=$(aws sts assume-role \
		--role-arn ${TARGET_ROLE} \
		--role-session-name ${ROLE_SESSION_NAME} \
		--query "Credentials.[AccessKeyId,SecretAccessKey,SessionToken]" \
		--output text)
export $(printf "AWS_ACCESS_KEY_ID=%s AWS_SECRET_ACCESS_KEY=%s AWS_SESSION_TOKEN=%s" $ASSUME_ROLE_CREDS)
kubectl $@

import { StackProps } from "aws-cdk-lib";

export interface CustomStackProps extends StackProps {
    deploymentName: string
}
import {Constants} from "../constants";
import * as ssm from "aws-cdk-lib/aws-ssm";

export class Ec2Architecture {

    static readonly X86_64 = new Ec2Architecture('x86_64', 't2.small')
    static readonly ARM_64 = new Ec2Architecture('arm64', 't4g.small')
    private static readonly PARAMETER_STORE_AMI = "/custom/ami/al2023"

    _label: string
    _instanceType: string

    constructor(label: string, instanceType: string) {
        this._label = label
        this._instanceType = instanceType
    }

    public get label() {
        return this._label;
    }

    public get instanceType() {
        return this._instanceType;
    }

    getCustomAmiParameterStoreName(): string {
        return `${Ec2Architecture.PARAMETER_STORE_AMI}/${process.env.TARGET_ARCHITECTURE}`
    }

    getCustomAmiParameterStoreArn(): string {
        let parameterStoreName = this.getCustomAmiParameterStoreName();
        return `arn:aws:ssm:${process.env.AWS_REGION}:${process.env.CICD_ACCOUNT_ID}:parameter${parameterStoreName}`
    }

    getBaseAmiParameterStore(): string {
        return `/aws/service/ami-amazon-linux-latest/al2023-ami-kernel-6.1-${process.env.TARGET_ARCHITECTURE}`;
    }
}
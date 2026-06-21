import {Constants} from "../constants";

export class Ec2Architecture {

    static readonly X86_64 = new Ec2Architecture('x86_64', 't2.small')
    static readonly ARM_64 = new Ec2Architecture('arm64', 't4g.small')
    private static readonly PARAMETER_STORE_AMI = "/custom/ami/al2023"

    _label:string
    _instanceType:string

    constructor(label:string, instanceType:string){
        this._label = label
        this._instanceType = instanceType
    }

    public get label() {
        return this._label;
    }

    public get instanceType() {
        return this._instanceType;
    }

    getParameterStoreName():string{
        if (process.env.TARGET_ARCHITECTURE == Ec2Architecture.X86_64.label || process.env.TARGET_ARCHITECTURE == Ec2Architecture.ARM_64.label){
            return `${Ec2Architecture.PARAMETER_STORE_AMI}/${process.env.TARGET_ARCHITECTURE}`
        }
        throw new Error('TARGET_ARCHITECTURE must be a filled');
    }

    getParameterStoreArn():string{
        if (process.env.TARGET_ARCHITECTURE == Ec2Architecture.X86_64.label || process.env.TARGET_ARCHITECTURE == Ec2Architecture.ARM_64.label){
            let parameterStoreName = this.getParameterStoreName();
            return `arn:aws:ssm:${process.env.AWS_REGION}:${process.env.CICD_ACCOUNT_ID}:parameter${parameterStoreName}`
        }
        throw new Error('TARGET_ARCHITECTURE must be a filled');
    }
}
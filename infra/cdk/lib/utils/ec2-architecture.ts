export class Ec2Architecture {

    static readonly X86_64 = new Ec2Architecture('x86_64', 't2.small')
    static readonly ARM_64 = new Ec2Architecture('arm64', 't4g.small')

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
}
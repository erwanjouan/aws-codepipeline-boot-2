import { CfnComponent } from "aws-cdk-lib/aws-imagebuilder";
import { Construct } from "constructs";
import path = require("path");
import fs = require('fs');
import { Constants } from "../constants";

export class BinariesComponent extends Construct  {

    cfnComponent: CfnComponent;

    constructor(scope: Construct, id: string) {
        super(scope, id);
        const data = fs.readFileSync(path.join('lib', 'ec2-image-builder', 'template', 'binaries-component.yml'), { encoding: 'utf-8' })
        const cfnComponent = new CfnComponent(this, 'binariesComponent', {
            name: Constants.PROJECT_NAME + '-binaries',
            changeDescription: "Installs base agents",
            platform: "Linux",
            description: "Installs base agents",
            data: data,
            version: "1.0.0",
            supportedOsVersions: ["Amazon Linux 2"],
            tags: {
                'Name': Constants.PROJECT_NAME
            }
        })
        this.arn = cfnComponent.attrArn
    }
    arn: string;
}
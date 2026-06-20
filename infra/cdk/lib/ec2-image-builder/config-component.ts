import { Construct } from "constructs";
import path = require("path");
import fs = require('fs');
import { Constants } from "../constants";
import { CfnComponent } from "aws-cdk-lib/aws-imagebuilder";

export class ConfigComponent extends Construct {

    cfnComponent: CfnComponent;

    constructor(scope: Construct, id: string) {
        super(scope, id);
        const data = fs.readFileSync(path.join('lib','ec2-image-builder','template','config-component.yml'), { encoding: 'utf-8' })
        const cfnComponent = new CfnComponent(this, 'configComponent', {
            name: `${process.env.PROJECT_NAME}-config`,
            changeDescription: "Installs base agents configuration",
            platform: "Linux",
            description: "Installs base agents configuration",
            data: data,
            version: "1.0.0",
            supportedOsVersions: ["Amazon Linux 2023"],
            tags: {
                'Name': `${process.env.PROJECT_NAME}`
            }
        })   
        this.arn = cfnComponent.attrArn
    }
    arn: string;
}
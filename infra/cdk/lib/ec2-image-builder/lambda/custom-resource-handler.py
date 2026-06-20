import boto3
import logging as log

log.getLogger().setLevel(log.INFO)


def main(event, context):
    log.info('Input event: %s', event)

    if event['RequestType'] not in ['Create', 'Update']:
        return {'Data': {'Response': 'no op'}}

    props = event['ResourceProperties']
    parameter_store_name = props['ParameterStoreName']
    ami_id = props['AmiId']

    log.info('parameterStoreName %s', parameter_store_name)
    log.info('amiId %s', ami_id)

    ssm = boto3.client('ssm')
    ssm.put_parameter(
        Name=parameter_store_name,
        Value=ami_id,
        Type='String',
        Overwrite=True,
        DataType='text',
    )

    message = '{} successfully written to {}'.format(ami_id, parameter_store_name)
    log.info(message)
    return {'Data': {'Response': message}}

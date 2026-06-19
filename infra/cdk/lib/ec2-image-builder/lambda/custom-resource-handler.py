import boto3

def main(event, context):
    import logging as log
    log.getLogger().setLevel(log.INFO)

    # This needs to change if there are to be multiple resources in the same stack
    physical_id = 'TheOnlyCustomResource'
    message = 'no op'

    try:
        log.info('Input event: %s', event)

        # Check if this is a Create and we're failing Creates
        if event['RequestType'] == 'Create' and event['ResourceProperties'].get('FailCreate', False):
            raise RuntimeError('Create failure requested')

        # Do the thing
        if event['RequestType'] is not None and event['RequestType'] in ['Create','Update']:
            props = event['ResourceProperties']
            
            parameterStoreName = props['ParameterStoreName']
            log.info('parameterStoreName {}'.format(parameterStoreName))
            
            amiId = props['AmiId']
            log.info('amiId {}'.format(amiId))

            ssm = boto3.client('ssm')

            response = ssm.put_parameter(
                Name=parameterStoreName,
                Value=amiId,
                Type='String',
                Overwrite=True, 
                DataType='text',
            )
            message = '{} was successfully written to parameter store entry : {}'.format(amiId, parameterStoreName)
        attributes = {
            'Response': message
        }
        return { 'Data': attributes }
    except Exception as e:
        log.exception(e)
        return { 
                'Data': {
                    'Response': 'exception occured : {}'.format(e)
                } 
        }
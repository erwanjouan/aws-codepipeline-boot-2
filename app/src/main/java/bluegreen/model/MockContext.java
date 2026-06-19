package bluegreen.model;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class MockContext implements Context {
    @Override
    public String getAwsRequestId() {
        return "AwsRequestId";
    }

    @Override
    public String getLogGroupName() {
        return "LogGroupName";
    }

    @Override
    public String getLogStreamName() {
        return "LogStreamName";
    }

    @Override
    public String getFunctionName() {
        return "FunctionName";
    }

    @Override
    public String getFunctionVersion() {
        return "FunctionVersion";
    }

    @Override
    public String getInvokedFunctionArn() {
        return "InvokedFunctionArn";
    }

    @Override
    public CognitoIdentity getIdentity() {
        return null;
    }

    @Override
    public ClientContext getClientContext() {
        return null;
    }

    @Override
    public int getRemainingTimeInMillis() {
        return 0;
    }

    @Override
    public int getMemoryLimitInMB() {
        return 0;
    }

    @Override
    public LambdaLogger getLogger() {
        return null;
    }
}

package bluegreen.conf.lambda;

import bluegreen.conf.AwsConfiguration;
import bluegreen.model.MockContext;
import com.amazonaws.services.lambda.runtime.Context;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.context.annotation.RequestScope;
import software.amazon.awssdk.services.lambda.LambdaClient;

import jakarta.servlet.http.HttpServletRequest;

import static bluegreen.model.Constant.API_GW_LAMBDA_PROFILE;
import static bluegreen.model.Constant.DYNAMODB_LAMBDA_PROFILE;
import static bluegreen.model.Constant.SAM_LAMBDA_JAR_PROFILE;
import static com.amazonaws.serverless.proxy.RequestReader.LAMBDA_CONTEXT_PROPERTY;

@Configuration
@Profile({API_GW_LAMBDA_PROFILE, DYNAMODB_LAMBDA_PROFILE, SAM_LAMBDA_JAR_PROFILE})
public class ApiGwLambdaConfiguration implements AwsConfiguration {

    @Bean
    public LambdaClient lambdaClient() {
        return LambdaClient.builder()
                .region(REGION)
                .build();
    }

    @RequestScope
    @Bean
    public Context context(final HttpServletRequest httpServletRequest) {
        final Context context = (Context) httpServletRequest.getAttribute(LAMBDA_CONTEXT_PROPERTY);
        if (context == null) {
            return new MockContext();
        }
        return context;
    }
}

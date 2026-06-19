package bluegreen.service.ec2;

import bluegreen.junit.MockitoTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.BatchGetDeploymentTargetsRequest;
import software.amazon.awssdk.services.codedeploy.model.BatchGetDeploymentTargetsResponse;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WatchEc2CodeDeployServiceTest extends MockitoTest<WatchEc2CodeDeployService> {

    @Mock
    private CodeDeployClient codeDeployClient;

    @Test
    void batchGetDeploymentTargets() {
        final String deploymentId = "deploymentId";
        final String targetId = "targetId";
        final BatchGetDeploymentTargetsResponse response = BatchGetDeploymentTargetsResponse.builder().build();

        doReturn(response).when(this.codeDeployClient).batchGetDeploymentTargets(any(BatchGetDeploymentTargetsRequest.class));

        assertEquals(Optional.empty(), this.test.batchGetDeploymentTargets(deploymentId, targetId));
    }
}
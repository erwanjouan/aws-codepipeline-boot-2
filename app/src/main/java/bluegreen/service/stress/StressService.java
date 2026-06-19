package bluegreen.service.stress;

import com.martensigwart.fakeload.FakeLoad;
import com.martensigwart.fakeload.FakeLoadExecutor;
import com.martensigwart.fakeload.FakeLoadExecutors;
import com.martensigwart.fakeload.FakeLoads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

import java.util.concurrent.TimeUnit;

import static bluegreen.model.Constant.ALB_ECS_EC2_PROFILE;
import static bluegreen.model.Constant.ALB_ECS_FARGATE_PROFILE;
import static bluegreen.model.Constant.EKS_FARGATE_PROFILE;

@Service
@Profile({ALB_ECS_EC2_PROFILE, ALB_ECS_FARGATE_PROFILE, EKS_FARGATE_PROFILE})
public class StressService {
    @Autowired
    private SsmClient ssmClient;
    public static final int PARAM_POLLING_INTERVAL_MILLIS = 10000;
    public static final int CPU_PERCENT = 95;
    public static final int MARGIN = 500;
    private final FakeLoadExecutor executor = FakeLoadExecutors.newDefaultExecutor();
    private static final Logger log = LoggerFactory.getLogger(StressService.class);
    public static final String PARAMETER_STORE_ENTRY = "/custom/stress";

    @Scheduled(fixedRate = PARAM_POLLING_INTERVAL_MILLIS)
    private void stress() {
        final Boolean stressed = this.isStressed();
        log.info("Stress enabled {}", stressed);
        if (Boolean.TRUE.equals(stressed)) {
            final FakeLoad fakeload = this.getFakeLoadConfig();
            this.executor.executeAsync(fakeload);
        }
    }

    private FakeLoad getFakeLoadConfig() {
        final int fakeLoadDuration = PARAM_POLLING_INTERVAL_MILLIS - MARGIN;
        return FakeLoads.create()
                .lasting(fakeLoadDuration, TimeUnit.MILLISECONDS)
                .withCpu(CPU_PERCENT);
    }

    private Boolean isStressed() {
        final GetParameterRequest parameterRequest = GetParameterRequest.builder()
                .name(PARAMETER_STORE_ENTRY)
                .build();
        final GetParameterResponse parameterResponse = this.ssmClient.getParameter(parameterRequest);
        final String value = parameterResponse.parameter().value();
        return Boolean.parseBoolean(value);
    }
}
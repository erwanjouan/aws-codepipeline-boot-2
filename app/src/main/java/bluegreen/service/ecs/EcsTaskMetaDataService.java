package bluegreen.service.ecs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class EcsTaskMetaDataService {

    public static final String DEFAULT_TASKID = "unknown";
    public static final String DEFAULT_TASK_ARN = "unknown";
    public static final String TASK_ARN = "TaskARN";

    @Autowired
    private EcsTaskMetaDataV4 ecsTaskMetaDataV4;

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, Object> getTaskMap() {
        try {
            final String task = this.ecsTaskMetaDataV4.task();
            return this.objectMapper.readValue(task, new TypeReference<>() {
            });
        } catch (final Exception exception) {
            log.error("getTaskMap", exception);
            return Map.of(TASK_ARN, DEFAULT_TASK_ARN);
        }
    }

    public String getTaskId() {
        try {
            return Optional.ofNullable(this.getTaskArn())
                    .map(s -> s.split("/"))
                    .map(s -> s[2])
                    .orElse(DEFAULT_TASKID);
        } catch (final Exception exception) {
            return DEFAULT_TASKID;
        }
    }

    public String getTaskArn() {
        return (String) this.getTaskMap().get(TASK_ARN);
    }

    public String stats() {
        try {
            return this.ecsTaskMetaDataV4.stats();
        } catch (final Exception exception) {
            return "no stat";
        }
    }
}

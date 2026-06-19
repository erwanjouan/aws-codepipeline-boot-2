package bluegreen.service.ecs;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "ecsTaskMetaData", url = "${ECS_CONTAINER_METADATA_URI_V4}")
public interface EcsTaskMetaDataV4 {
 
    @RequestMapping(method = RequestMethod.GET, value = "/task")
    String task();

    @RequestMapping(method = RequestMethod.GET, value = "/taskWithTags")
    String taskWithTags();

    @RequestMapping(method = RequestMethod.GET, value = "/stats")
    String stats();

}

package bluegreen.web.ecs;

import bluegreen.model.EcsFargateAlbControlDto;
import bluegreen.service.ecs.EcsTaskMetaDataV4;
import bluegreen.service.ecs.WatchEcsFargateAlbService;
import bluegreen.web.AbstractBlueGreenController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static bluegreen.model.Constant.ALB_ECS_FARGATE_PROFILE;
import static bluegreen.model.Constant.ID;

@Controller
@Profile(ALB_ECS_FARGATE_PROFILE)
public class EcsFargateController extends AbstractBlueGreenController {

    @Autowired
    private WatchEcsFargateAlbService watchEcsFargateAlbService;

    @Autowired
    private EcsTaskMetaDataV4 ecsTaskMetaDataV4;

    @GetMapping("/" + ALB_ECS_FARGATE_PROFILE)
    public String ecsPage(final Model model) {
        model.addAttribute("color", this.color);
        final EcsFargateAlbControlDto ecsFargateAlbControlDto = this.watchEcsFargateAlbService.watch();
        model.addAttribute("ecsFargateAlbControlDto", ecsFargateAlbControlDto);
        return ALB_ECS_FARGATE_PROFILE;
    }

    @GetMapping("/" + ALB_ECS_FARGATE_PROFILE + "/data")
    @ResponseBody
    public Map<String, Object> ecsData() {
        final EcsFargateAlbControlDto dto = this.watchEcsFargateAlbService.watch();
        final List<Map<String, Object>> tasks = dto.getControlTable().getRows().stream()
                .map(row -> {
                    final Map<String, Object> task = new HashMap<>();
                    task.put("isCurrent", row.get("isCurrent"));
                    task.put("id", String.valueOf(row.getOrDefault(ID, "")));
                    task.put("taskDefinition", String.valueOf(row.getOrDefault("taskDefinition", "")));
                    task.put("capacityProvider", String.valueOf(row.getOrDefault("capacityProvider", "")));
                    task.put("lastStatus", String.valueOf(row.getOrDefault("lastStatus", "")));
                    task.put("createdAt", String.valueOf(row.getOrDefault("createdAt", "")));
                    return task;
                })
                .collect(Collectors.toList());
        final Map<String, Object> result = new HashMap<>();
        result.put("taskArn", dto.getTaskArn() != null ? dto.getTaskArn() : "unknown");
        result.put("cpu", dto.getCpu() != null ? dto.getCpu() : 0);
        result.put("tasks", tasks);
        result.put("events", dto.getEvents() != null ? dto.getEvents() : List.of());
        return result;
    }

    @GetMapping("taskWithTags")
    public String taskWithTags() {
        return this.ecsTaskMetaDataV4.taskWithTags();
    }

    @GetMapping("task")
    public String task(final Model model) {
        final String task = this.ecsTaskMetaDataV4.task();
        model.addAttribute("taskResponse", task);
        return "task";
    }

    @GetMapping("stats")
    public String stats() {
        return this.ecsTaskMetaDataV4.stats();
    }

}

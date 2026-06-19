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

import static bluegreen.model.Constant.ALB_ECS_FARGATE_PROFILE;

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

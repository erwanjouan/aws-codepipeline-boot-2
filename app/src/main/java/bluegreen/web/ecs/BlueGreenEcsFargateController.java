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

import static bluegreen.model.Constant.BLUE_GREEN_ECS_FARGATE_PROFILE;

@Controller
@Profile(BLUE_GREEN_ECS_FARGATE_PROFILE)
public class BlueGreenEcsFargateController extends AbstractBlueGreenController {

    @Autowired
    private WatchEcsFargateAlbService watchEcsFargateAlbService;

    @Autowired
    private EcsTaskMetaDataV4 ecsTaskMetaDataV4;

    @GetMapping("/" + BLUE_GREEN_ECS_FARGATE_PROFILE)
    public String blueGreenEcsPage(final Model model) {
        model.addAttribute("color", this.color);
        final EcsFargateAlbControlDto ecsFargateAlbControlDto = this.watchEcsFargateAlbService.watch();
        model.addAttribute("ecsFargateAlbControlDto", ecsFargateAlbControlDto);
        return BLUE_GREEN_ECS_FARGATE_PROFILE;
    }
}

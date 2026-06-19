package bluegreen.web.eks;

import bluegreen.model.EksControlDto;
import bluegreen.service.eks.WatchEksService;
import bluegreen.web.AbstractBlueGreenController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static bluegreen.model.Constant.EKS_FARGATE_PROFILE;

@Controller
@Profile(EKS_FARGATE_PROFILE)
public class EksFargateController extends AbstractBlueGreenController {

    @Autowired
    private WatchEksService watchEksService;

    @GetMapping("/" + EKS_FARGATE_PROFILE)
    public String ecsPage(final Model model) {
        model.addAttribute("color", this.color);
        final EksControlDto eksControlDto = this.watchEksService.watch();
        model.addAttribute("eksControlDto", eksControlDto);
        return EKS_FARGATE_PROFILE;
    }

}

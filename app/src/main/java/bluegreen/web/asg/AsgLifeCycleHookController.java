package bluegreen.web.asg;

import bluegreen.model.Ec2ControlDto;
import bluegreen.service.asg.WatchAsgUpdatePolicyService;
import bluegreen.web.AbstractBlueGreenController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static bluegreen.model.Constant.ASG_LIFECYCLE_HOOK_PROFILE;

@Controller
@Profile(ASG_LIFECYCLE_HOOK_PROFILE)
public class AsgLifeCycleHookController extends AbstractBlueGreenController {

    @Autowired
    private WatchAsgUpdatePolicyService watchAsgUpdatePolicyService;

    @GetMapping("/" + ASG_LIFECYCLE_HOOK_PROFILE)
    public String asgUpdatePolicyPage(final Model model) {
        model.addAttribute("color", this.color);
        final Ec2ControlDto ec2ControlDto = this.watchAsgUpdatePolicyService.watch();
        model.addAttribute("ec2ControlDto", ec2ControlDto);
        return ASG_LIFECYCLE_HOOK_PROFILE;
    }

}

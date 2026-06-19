package bluegreen.web.asg;

import bluegreen.model.Ec2ControlDto;
import bluegreen.service.asg.WatchAsgUpdatePolicyService;
import bluegreen.web.AbstractBlueGreenController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static bluegreen.model.Constant.CLOUDFRONT_PROFILE;

@Controller
@Profile(CLOUDFRONT_PROFILE)
public class AsgReplacingUpdateController extends AbstractBlueGreenController {

    @Autowired
    private WatchAsgUpdatePolicyService watchAsgUpdatePolicyService;

    @GetMapping("/" + CLOUDFRONT_PROFILE)
    public String asgUpdatePolicyPage(final Model model) {
        model.addAttribute("color", this.color);
        final Ec2ControlDto ec2ControlDto = this.watchAsgUpdatePolicyService.watch();
        model.addAttribute("ec2ControlDto", ec2ControlDto);
        return CLOUDFRONT_PROFILE;
    }

}

package bluegreen.web.ec2;

import bluegreen.model.CodeDeployControlDto;
import bluegreen.service.ec2.WatchEc2CodeDeployService;
import bluegreen.web.AbstractBlueGreenController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static bluegreen.model.Constant.ASG_CODE_DEPLOY_EC2_PROFILE;

@Controller
@Profile(ASG_CODE_DEPLOY_EC2_PROFILE)
public class Ec2CodeDeployController extends AbstractBlueGreenController {

    @Autowired
    private WatchEc2CodeDeployService watchEc2CodeDeployService;

    @GetMapping("/" + ASG_CODE_DEPLOY_EC2_PROFILE)
    public String ec2CodeDeployPage(final Model model) {
        model.addAttribute("color", this.color);
        final CodeDeployControlDto codeDeployControlDto = this.watchEc2CodeDeployService.watch();
        model.addAttribute("codeDeployControlDto", codeDeployControlDto);
        return ASG_CODE_DEPLOY_EC2_PROFILE;
    }

}

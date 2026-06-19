package bluegreen.web.opsworks;

import bluegreen.model.OpsWorksStacksControlDto;
import bluegreen.service.opsworks.WatchOpsWorksStacksService;
import bluegreen.web.AbstractBlueGreenController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static bluegreen.model.Constant.OPSWORKS_STACKS_PROFILE;

@Controller
@Profile(OPSWORKS_STACKS_PROFILE)
public class OpsWorksController extends AbstractBlueGreenController {

    @Autowired
    private WatchOpsWorksStacksService watchOpsWorksStacksService;

    @GetMapping("/" + OPSWORKS_STACKS_PROFILE)
    public String opsWorksPage(final Model model) {
        model.addAttribute("color", this.color);
        final OpsWorksStacksControlDto opsWorksStacksControlDto = this.watchOpsWorksStacksService.watch();
        model.addAttribute("opsWorksControlDto", opsWorksStacksControlDto);
        return this.deploymentView();
    }

    private String deploymentView() {
        return OPSWORKS_STACKS_PROFILE;
    }

}

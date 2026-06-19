package bluegreen.web.eb;

import bluegreen.model.EbControlDto;
import bluegreen.service.eb.WatchEbService;
import bluegreen.web.AbstractBlueGreenController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

public abstract class AbstractEbEc2Controller extends AbstractBlueGreenController {

    public static final String EB_EC2_DOCKER_VIEW = "eb-ec2-docker";

    @Autowired
    protected WatchEbService watchEbService;

    protected void fillModel(final Model model) {
        model.addAttribute("color", this.color);
        final EbControlDto ebControlDto = this.watchEbService.watch();
        model.addAttribute("ebControlDto", ebControlDto);
    }

    protected String deploymentView() {
        return EB_EC2_DOCKER_VIEW;
    }
}

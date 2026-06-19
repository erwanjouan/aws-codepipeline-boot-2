package bluegreen.web.lightsail;

import bluegreen.model.LightSailControlDto;
import bluegreen.service.lightsail.WatchLightSailContainerService;
import bluegreen.web.AbstractBlueGreenController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static bluegreen.model.Constant.LIGHTSAIL_CONTAINER_PROFILE;

@Controller
@Profile(LIGHTSAIL_CONTAINER_PROFILE)
public class LightsailContainerController extends AbstractBlueGreenController {

    @Autowired
    private WatchLightSailContainerService watch;

    @GetMapping("/" + LIGHTSAIL_CONTAINER_PROFILE)
    public String lightSailContainerPage(final Model model) {
        model.addAttribute("color", this.color);
        final LightSailControlDto lightSailControlDto = this.watch.watch();
        model.addAttribute("lightSailControlDto", lightSailControlDto);
        return LIGHTSAIL_CONTAINER_PROFILE;
    }
}

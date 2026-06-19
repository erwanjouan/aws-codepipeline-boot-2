package bluegreen.web.lightsail;

import bluegreen.model.AppRunnerControlDto;
import bluegreen.service.apprunner.WatchAppRunnerContainerService;
import bluegreen.web.AbstractBlueGreenController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static bluegreen.model.Constant.APPRUNNER_CONTAINER_PROFILE;

@Controller
@Profile(APPRUNNER_CONTAINER_PROFILE)
public class AppRunnerContainerController extends AbstractBlueGreenController {

    @Autowired
    private WatchAppRunnerContainerService watch;

    @GetMapping("/" + APPRUNNER_CONTAINER_PROFILE)
    public String appRunnerContainerPage(final Model model) {
        model.addAttribute("color", this.color);
        final AppRunnerControlDto appRunnerControlDto = this.watch.watch();
        model.addAttribute("appRunnerControlDto", appRunnerControlDto);
        return APPRUNNER_CONTAINER_PROFILE;
    }
}

package bluegreen.web.eb;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static bluegreen.model.Constant.EB_SINGLE_DOCKER_PROFILE;

@Controller
@Profile(EB_SINGLE_DOCKER_PROFILE)
public class EbEc2SingleDockerController extends AbstractEbEc2Controller {

    @GetMapping("/" + EB_SINGLE_DOCKER_PROFILE)
    public String singleDockerPage(final Model model) {
        this.fillModel(model);
        return this.deploymentView();
    }

}

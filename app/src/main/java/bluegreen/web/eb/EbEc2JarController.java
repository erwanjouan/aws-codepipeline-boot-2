package bluegreen.web.eb;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static bluegreen.model.Constant.EB_EC2_JAR_PROFILE;

@Controller
@Profile(EB_EC2_JAR_PROFILE)
public class EbEc2JarController extends AbstractEbEc2Controller {

    @GetMapping("/" + EB_EC2_JAR_PROFILE)
    public String singleDockerPage(final Model model) {
        this.fillModel(model);
        return EB_EC2_JAR_PROFILE;
    }

}

package bluegreen.web.cognito;

import bluegreen.web.AbstractBlueGreenController;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static bluegreen.model.Constant.COGNITO_SOCIAL_IDP_PROFILE;

@Controller
@Profile(COGNITO_SOCIAL_IDP_PROFILE)
public class CognitoSocialIdpController extends AbstractBlueGreenController {

    @GetMapping("/" + COGNITO_SOCIAL_IDP_PROFILE)
    public String cognitoPage(final Model model) {
        model.addAttribute("color", this.color);
        return COGNITO_SOCIAL_IDP_PROFILE;
    }
}

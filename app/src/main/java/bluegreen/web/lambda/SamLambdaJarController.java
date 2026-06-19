package bluegreen.web.lambda;

import bluegreen.model.ApiGwLambdaControlDto;
import bluegreen.service.lambda.WatchApiGwLambdaService;
import bluegreen.web.AbstractBlueGreenController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static bluegreen.model.Constant.SAM_LAMBDA_JAR_PROFILE;

@Controller
@Profile(SAM_LAMBDA_JAR_PROFILE)
public class SamLambdaJarController extends AbstractBlueGreenController {

    @Autowired
    private WatchApiGwLambdaService watchApiGwLambdaService;

    @GetMapping("/" + SAM_LAMBDA_JAR_PROFILE)
    public String apiGwPage(final Model model) {
        model.addAttribute("color", this.color);
        final ApiGwLambdaControlDto apiGwLambdaControlDto = this.watchApiGwLambdaService.watch();
        model.addAttribute("apiGwLambdaControlDto", apiGwLambdaControlDto);
        return SAM_LAMBDA_JAR_PROFILE;
    }

}

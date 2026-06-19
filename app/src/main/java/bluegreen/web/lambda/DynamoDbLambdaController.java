package bluegreen.web.lambda;

import bluegreen.model.exam.Exam;
import bluegreen.service.dynamodb.WatchDynamoDbLambdaService;
import bluegreen.web.AbstractBlueGreenController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static bluegreen.model.Constant.DYNAMODB_LAMBDA_PROFILE;

@Controller
@Profile(DYNAMODB_LAMBDA_PROFILE)
public class DynamoDbLambdaController extends AbstractBlueGreenController {

    @Autowired
    private WatchDynamoDbLambdaService watchDynamoDbLambdaService;

    @GetMapping("/" + DYNAMODB_LAMBDA_PROFILE)
    public String dynamoDbPage(final Model model) {
        final Exam exam = this.watchDynamoDbLambdaService.getExam();
        model.addAttribute("exam", exam);
        return DYNAMODB_LAMBDA_PROFILE;
    }

}

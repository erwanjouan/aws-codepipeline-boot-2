package bluegreen.service.dynamodb;

import bluegreen.dao.dynamodb.DynamoDbDao;
import bluegreen.model.exam.Exam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import static bluegreen.model.Constant.DYNAMODB_LAMBDA_PROFILE;
import static bluegreen.model.Constant.PROJECT_DEPLOYMENT_NAME_ENV;

@Service
@Profile(DYNAMODB_LAMBDA_PROFILE)
public class WatchDynamoDbLambdaService {
    
    @Value(PROJECT_DEPLOYMENT_NAME_ENV)
    private String projectDeploymentName;

    @Autowired
    private DynamoDbDao dynamoDbDao;

    public Exam getExam() {
        return this.dynamoDbDao.getExam();
    }

}

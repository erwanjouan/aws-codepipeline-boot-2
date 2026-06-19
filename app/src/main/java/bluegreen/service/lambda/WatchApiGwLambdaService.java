package bluegreen.service.lambda;

import bluegreen.model.ApiGwLambdaControlDto;
import bluegreen.model.ControlTable;
import bluegreen.service.WatchAwsService;
import com.amazonaws.services.lambda.runtime.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.AliasConfiguration;
import software.amazon.awssdk.services.lambda.model.ListAliasesRequest;
import software.amazon.awssdk.services.lambda.model.ListAliasesResponse;
import software.amazon.awssdk.services.lambda.model.ListVersionsByFunctionRequest;
import software.amazon.awssdk.services.lambda.model.ListVersionsByFunctionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static bluegreen.model.Constant.API_GW_LAMBDA_PROFILE;
import static bluegreen.model.Constant.NAME;
import static bluegreen.model.Constant.PROJECT_DEPLOYMENT_NAME_ENV;
import static bluegreen.model.Constant.SAM_LAMBDA_JAR_PROFILE;


@Service
@Profile({API_GW_LAMBDA_PROFILE, SAM_LAMBDA_JAR_PROFILE})
public class WatchApiGwLambdaService implements WatchAwsService<ApiGwLambdaControlDto> {

    public static final String TABLE_NAME = "Api Gw Lambda";
    public static final List<String> HEADERS = List.of(NAME, "Version", "Aliases", "LastModified");

    @Autowired
    private Context context;

    @Autowired
    private LambdaClient lambdaClient;

    @Value(PROJECT_DEPLOYMENT_NAME_ENV)
    private String projectDeploymentName;

    @Override
    public ApiGwLambdaControlDto watch() {

        final List<Map<String, Object>> rows = this.getControlTableRows();

        final ControlTable controlTable = ControlTable.builder()
                .tableName(TABLE_NAME)
                .headers(HEADERS)
                .rows(rows)
                .build();
        return ApiGwLambdaControlDto.builder()
                .controlTable(controlTable)
                .context(this.context)
                .build();
    }

    private List<Map<String, Object>> getControlTableRows() {
        final List<Map<String, Object>> rows = new ArrayList<>();
        final ListVersionsByFunctionRequest listVersionsByFunctionRequest = ListVersionsByFunctionRequest.builder()
                .functionName(this.projectDeploymentName)
                .build();
        final ListVersionsByFunctionResponse listVersionsByFunctionResponse = this.lambdaClient.listVersionsByFunction(listVersionsByFunctionRequest);


        listVersionsByFunctionResponse
                .versions()
                .forEach(functionConfiguration -> {
                    final String version = functionConfiguration.version();
                    final ListAliasesRequest listAliasesRequest = ListAliasesRequest.builder()
                            .functionName(this.projectDeploymentName)
                            .functionVersion(version)
                            .build();
                    final ListAliasesResponse listAliasesResponse = this.lambdaClient.listAliases(listAliasesRequest);
                    final List<AliasConfiguration> aliases = listAliasesResponse.aliases();
                    final String aliasList = aliases.stream().map(AliasConfiguration::name).collect(Collectors.joining(" "));
                    rows.add(Map.of(NAME, functionConfiguration.functionName(),
                            "Version", version,
                            "Aliases", aliasList,
                            "LastModified", functionConfiguration.lastModified()));
                });

        return rows;
    }
}

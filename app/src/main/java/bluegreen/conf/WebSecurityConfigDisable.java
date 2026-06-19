package bluegreen.conf;

import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static bluegreen.model.Constant.NON_COGNITO_SOCIAL_IDP_PROFILE;

@Configuration
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
@Profile(NON_COGNITO_SOCIAL_IDP_PROFILE)
public class WebSecurityConfigDisable {
}

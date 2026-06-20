package bluegreen.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static bluegreen.model.Constant.NON_COGNITO_SOCIAL_IDP_PROFILE;

@Configuration
@Profile(NON_COGNITO_SOCIAL_IDP_PROFILE)
public class WebSecurityConfigDisable {

    @Bean
    public SecurityFilterChain permitAllSecurityFilterChain(final HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}

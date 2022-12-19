package io.micronaut.security.formlogin.endpoints;

import io.micronaut.http.annotation.Controller;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.endpoints.LoginControllerConfigurationProperties;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;

import java.util.Map;

@Controller("${" + LoginControllerConfigurationProperties.PREFIX + ".path:/login}")
public class FormLoginController {
    @Secured(SecurityRule.IS_ANONYMOUS)
    @View("io/micronaut/security/formlogin/loginForm")
    Map<String, Object> loginForm() {
        return Map.of();
    }
}

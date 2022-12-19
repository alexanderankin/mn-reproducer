package gallery.sharer.filter;

import gallery.sharer.security.SavingSessionLoginHandler;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.endpoints.LoginControllerConfigurationProperties;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.session.http.SessionForRequest;
import io.micronaut.views.View;
import jakarta.inject.Inject;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

// @Requires(env = "optional")
@Controller("${" + LoginControllerConfigurationProperties.PREFIX + ".path:/login}")
public class LoginFormController {

    @Value("${" + LoginControllerConfigurationProperties.PREFIX + ".path:/login}")
    String authenticationUrl;

    @Inject
    List<AuthenticationProvider> authenticationProviders;

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Get
    @View("login")
    Config loginForm() {
        return new Config()
                .setAuthenticationUrl(authenticationUrl);
    }

    @Accessors(chain = true)
    @Data
    static class Config {
        String authenticationUrl = "/login";
        String usernameParameter = "username";
        String passwordParameter = "password";
        String rememberMeParameter = "rememberMe";
        boolean formLoginEnabled = true;
    }

    private String generateLoginPageHtml(HttpRequest<?> request, boolean loginError, boolean logoutSuccess) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"en\">\n");
        sb.append("  <head>\n");
        sb.append("    <meta charset=\"utf-8\">\n");
        sb.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n");
        sb.append("    <meta name=\"description\" content=\"\">\n");
        sb.append("    <meta name=\"author\" content=\"\">\n");
        sb.append("    <title>Please sign in</title>\n");
        sb.append("    <link href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css\" "
                + "rel=\"stylesheet\" integrity=\"sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M\" crossorigin=\"anonymous\">\n");
        sb.append("    <link href=\"https://getbootstrap.com/docs/4.0/examples/signin/signin.css\" "
                + "rel=\"stylesheet\" crossorigin=\"anonymous\"/>\n");
        sb.append("  </head>\n");
        sb.append("  <body>\n");
        sb.append("     <div class=\"container\">\n");
        if (this.formLoginEnabled()) {
            sb.append("      <form class=\"form-signin\" method=\"post\" action=\""
                    + this.authenticationUrl() + "\">\n");
            sb.append("        <h2 class=\"form-signin-heading\">Please sign in</h2>\n");
            sb.append(createError(loginError, getErrorMessage(request)) + createLogoutSuccess(logoutSuccess) + "        <p>\n");
            sb.append("          <label for=\"username\" class=\"sr-only\">Username</label>\n");
            sb.append("          <input type=\"text\" id=\"username\" name=\"" + this.usernameParameter()
                    + "\" class=\"form-control\" placeholder=\"Username\" required autofocus>\n");
            sb.append("        </p>\n");
            sb.append("        <p>\n");
            sb.append("          <label for=\"password\" class=\"sr-only\">Password</label>\n");
            sb.append("          <input type=\"password\" id=\"password\" name=\"" + this.passwordParameter()
                    + "\" class=\"form-control\" placeholder=\"Password\" required>\n");
            sb.append("        </p>\n");
            sb.append(createRememberMe(this.rememberMeParameter()) + renderHiddenInputs(request));
            sb.append("        <button class=\"btn btn-lg btn-primary btn-block\" type=\"submit\">Sign in</button>\n");
            sb.append("      </form>\n");
        }
        if (this.oauth2LoginEnabled()) {
            sb.append("<h2 class=\"form-signin-heading\">Login with OAuth 2.0</h2>");
            sb.append(createError(loginError, getErrorMessage(request)));
            sb.append(createLogoutSuccess(logoutSuccess));
            sb.append("<table class=\"table table-striped\">\n");
            for (Map.Entry<String, String> clientAuthenticationUrlToClientName : this.oauth2AuthenticationUrlToClientName().entrySet()) {
                sb.append(" <tr><td>");
                String url = clientAuthenticationUrlToClientName.getKey();
                sb.append("<a href=\"").append(url).append("\">");
                String clientName = htmlEscape(clientAuthenticationUrlToClientName.getValue());
                sb.append(clientName);
                sb.append("</a>");
                sb.append("</td></tr>\n");
            }
            sb.append("</table>\n");
        }
        if (this.saml2LoginEnabled()) {
            sb.append("<h2 class=\"form-signin-heading\">Login with SAML 2.0</h2>");
            sb.append(createError(loginError, getErrorMessage(request)));
            sb.append(createLogoutSuccess(logoutSuccess));
            sb.append("<table class=\"table table-striped\">\n");
            for (Map.Entry<String, String> relyingPartyUrlToName : this.saml2AuthenticationUrlToProviderName().entrySet()) {
                sb.append(" <tr><td>");
                String url = relyingPartyUrlToName.getKey();
                sb.append("<a href=\"").append(url).append("\">");
                String partyName = htmlEscape(relyingPartyUrlToName.getValue());
                sb.append(partyName);
                sb.append("</a>");
                sb.append("</td></tr>\n");
            }
            sb.append("</table>\n");
        }
        sb.append("</div>\n");
        sb.append("</body></html>");
        return sb.toString();
    }

    private String getErrorMessage(HttpRequest<?> request) {
        return SessionForRequest.find(request)
                .map(s -> s.get(SavingSessionLoginHandler.FAILURE_ATTRIBUTE, String.class))
                .flatMap(Function.identity())
                .orElse("Invalid credentials");
    }

    private boolean formLoginEnabled() {
        return false;
    }

    private String authenticationUrl() {
        return null;
    }

    private String usernameParameter() {
        return null;
    }

    private String passwordParameter() {
        return null;
    }

    private String createRememberMe(Object rememberMeParameter) {
        return "new char[0];";
    }

    private String rememberMeParameter() {
        return null;
    }

    private String renderHiddenInputs(HttpRequest<?> request) {
        return null;
    }

    private boolean oauth2LoginEnabled() {
        return false;
    }

    private Map<String, String> oauth2AuthenticationUrlToClientName() {
        return Map.of();
    }

    private boolean saml2LoginEnabled() {
        return false;
    }

    private String createError(boolean loginError, String errorMsg) {
        return "false";
    }

    private String createLogoutSuccess(boolean logoutSuccess) {
        return "false";
    }

    private Map<String, String> saml2AuthenticationUrlToProviderName() {
        return Map.of();
    }

    private String htmlEscape(String value) {
        return null;
    }

}

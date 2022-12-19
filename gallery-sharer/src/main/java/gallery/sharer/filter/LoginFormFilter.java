package gallery.sharer.filter;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.FilterChain;
import io.micronaut.http.filter.HttpFilter;
import io.micronaut.http.filter.ServerFilterPhase;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.oauth2.configuration.OauthClientConfigurationProperties;
import jakarta.inject.Inject;
import org.reactivestreams.Publisher;

import java.util.List;

@Requires(env = "optional")
@Filter(Filter.MATCH_ALL_PATTERN)
public class LoginFormFilter implements HttpFilter {

    @Inject
    List<OauthClientConfigurationProperties> properties;

    @Inject
    List<AuthenticationProvider> authenticationProviders;

    @Override
    public Publisher<? extends HttpResponse<?>> doFilter(HttpRequest<?> request,
                                                         FilterChain chain) {
        return chain.proceed(request);
    }

    @Override
    public int getOrder() {
        return ServerFilterPhase.SECURITY.before();
    }
}

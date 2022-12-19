package gallery.sharer.security;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@Requires(env = "optional")
@Singleton
public class AuthenticationProviderUserPassword implements AuthenticationProvider {

    @Override
    public Publisher<AuthenticationResponse> authenticate(HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
        return Mono.create(emitter -> {
            if (authenticationRequest.getIdentity().equals("user") && authenticationRequest.getSecret().equals("password")) {
                emitter.success(AuthenticationResponse.success("user"));
            } else {
                emitter.error(AuthenticationResponse.exception());
            }
        });
    }
}

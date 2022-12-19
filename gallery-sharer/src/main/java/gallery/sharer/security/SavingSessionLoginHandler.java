package gallery.sharer.security;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.config.RedirectConfiguration;
import io.micronaut.security.config.RedirectService;
import io.micronaut.security.errors.PriorToLoginPersistence;
import io.micronaut.security.session.SessionLoginHandler;
import io.micronaut.session.Session;
import io.micronaut.session.SessionStore;
import io.micronaut.session.http.SessionForRequest;

import java.util.Optional;
import java.util.function.Function;

@Replaces(SessionLoginHandler.class)
public class SavingSessionLoginHandler extends SessionLoginHandler {
    public static final String FAILURE_ATTRIBUTE = SavingSessionLoginHandler.class.getName() + ".FAILURE";
    private static final Optional<String> DEFAULT_MESSAGE = Optional.of("Authentication failed");

    private static Optional<String> getDefaultMessage() {
        return DEFAULT_MESSAGE;
    }

    public SavingSessionLoginHandler(RedirectConfiguration redirectConfiguration,
                                     SessionStore<Session> sessionStore,
                                     PriorToLoginPersistence priorToLoginPersistence,
                                     RedirectService redirectService) {
        super(redirectConfiguration, sessionStore, priorToLoginPersistence, redirectService);
    }

    @Override
    public MutableHttpResponse<?> loginFailed(AuthenticationResponse authenticationFailed, HttpRequest<?> request) {
        Optional.ofNullable(authenticationFailed)
                .map(AuthenticationResponse::getMessage)
                .flatMap(Function.identity())
                .or(SavingSessionLoginHandler::getDefaultMessage)
                .ifPresent(s -> SessionForRequest.findOrCreate(request, sessionStore)
                        .put(FAILURE_ATTRIBUTE, s));

        return super.loginFailed(authenticationFailed, request);
    }
}

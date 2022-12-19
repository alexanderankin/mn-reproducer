package gallery.sharer.filter;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.FilterChain;
import io.micronaut.http.filter.HttpFilter;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.web.router.resource.StaticResourceConfiguration;
import org.reactivestreams.Publisher;

import java.util.List;

@Requires(env = "optional")
@Filter
public class SpaFilter implements HttpFilter {
    private final List<ResourceLoader> resourceLoaders;

    public SpaFilter(List<StaticResourceConfiguration> configurations) {
        resourceLoaders = configurations.stream()
                .map(StaticResourceConfiguration::getResourceLoaders)
                .flatMap(List::stream)
                .toList();
    }

    @Override
    public Publisher<? extends HttpResponse<?>> doFilter(HttpRequest<?> request,
                                                         FilterChain chain) {
        String path = request.getPath();

        if (!(isApi(path) || hasStatic(path))) {
            return chain.proceed(fixToIndexHtml(request));
        }

        return chain.proceed(request);
    }

    private boolean isApi(String path) {
        return path.startsWith("/api");
    }

    private boolean hasStatic(String path) {
        return resourceLoaders.stream().anyMatch(l -> l.getResource(path).isPresent());
    }

    private HttpRequest<?> fixToIndexHtml(HttpRequest<?> request) {
        return (request instanceof MutableHttpRequest ? (MutableHttpRequest<?>) request : request.mutate())
                .uri(UriBuilder.of(request.getUri())
                        .path("/index.html")
                        .build());
    }
}

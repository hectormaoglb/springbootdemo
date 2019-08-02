package com.example.demo.route;

import com.example.demo.cfg.EndpointConfig;
import com.example.demo.cfg.RestConfig;
import com.example.demo.srv.GenericService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Optional;

@Configuration
public class GenericRouting {

    @Bean
    public RouterFunction<ServerResponse> getRouterFunction(GenericService service, RestConfig restConfig) {
        return restConfig
                .getEndpoints()
                .entrySet()
                .stream()
                .map(entry -> {
                    final String operation = entry.getKey();
                    final EndpointConfig endpointConfig = entry.getValue();
                    return RouterFunctions.route(
                            RequestPredicates.method(endpointConfig.getMethod()).and(RequestPredicates.path(endpointConfig.getPath())),
                            serverRequest -> service.processRequest(serverRequest, operation)
                    );
                })
                .reduce(null, (identity, routerFunction) -> Optional
                        .ofNullable(identity)
                        .map(idn -> idn.and(routerFunction))
                        .orElse(routerFunction));
    }
}

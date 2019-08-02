package com.example.demo.srv;

import com.example.demo.repo.GenericRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;


@Component
public class GenericService {

    private GenericRepository repository;

    @Autowired
    public GenericService(GenericRepository repository) {
        this.repository = repository;
    }

    public Mono<ServerResponse> processRequest(ServerRequest request, String operation) {
        return request
                .bodyToMono(Map.class)
                .defaultIfEmpty(new HashMap())
                .map(requestMap -> enrichRequest(request, requestMap))
                .flatMap(enrichedRequest -> repository.findData(enrichedRequest, operation))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromObject(response)));
    }

    private Map<String, Object> enrichRequest(ServerRequest request, Map body) {

        Map<String, Object> requestMap = new HashMap(body);

        request
                .headers()
                .asHttpHeaders()
                .entrySet()
                .forEach(entry ->
                        requestMap.put(entry.getKey(), entry.getValue().get(0)));

        request
                .queryParams()
                .entrySet()
                .forEach(entry ->
                        requestMap.put(entry.getKey(), entry.getValue().get(0)));

        request
                .pathVariables()
                .entrySet()
                .forEach(entry ->
                        requestMap.put(entry.getKey(), entry.getValue()));

        return requestMap;
    }

}

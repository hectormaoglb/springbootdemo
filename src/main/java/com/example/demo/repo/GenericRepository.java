package com.example.demo.repo;

import com.example.demo.cfg.OperationConfig;
import com.example.demo.cfg.ProxyConfig;
import com.example.demo.trans.GenericTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Repository
public class GenericRepository {

    private ProxyConfig proxyConfig;

    private GenericTranslator translator;

    private WebClient webClient;

    @Autowired
    public GenericRepository(ProxyConfig proxyConfig, GenericTranslator translator){
        this.proxyConfig = proxyConfig;
        this.translator = translator;
        webClient = WebClient.create();
    }

    public Mono<Map> findData(Map<String, Object> request, String operationName){
        final OperationConfig operation = proxyConfig.getOperations().get(operationName);

        String parsedURL = translator.parseString(operation.getUrl(), request);
        Map parsedPayload = translator.parseJson(operation.getPayload(), request);
        final List<Integer> validStatusCodes = Optional
                .ofNullable(operation.getValidStatusCodes())
                .orElseGet(ArrayList::new);

        return webClient
                .method(operation.getMethod())
                .uri(parsedURL)
                .headers(httpHeaders -> setHeaders(httpHeaders, operation, request))
                .body(Optional
                        .ofNullable(parsedPayload)
                        .filter(map -> ! map.isEmpty())
                        .map(BodyInserters::fromObject)
                        .orElse(BodyInserters.empty())
                )
                .exchange()
                .flatMap(clientResponse -> {
                    int statusCode = clientResponse.rawStatusCode();
                    if(validStatusCodes.contains(statusCode)){
                        return clientResponse
                                .bodyToMono(Map.class)
                                .map(webResponse -> {
                                    if(operation.isApplyTemplate()){
                                        return translator.parseThirdPartyResponse(request, webResponse, operationName);
                                    } else {
                                        return webResponse;
                                    }
                                });
                    } else {
                        return Mono.<Map>error(new RestClientResponseException(
                                "Invalid Response Status Code ",
                                statusCode,
                                "",
                                null,
                                null,
                                null));
                    }
                });
    }

    private void setHeaders(HttpHeaders headers, OperationConfig operation, Map<String, Object> request){
        Optional
                .ofNullable(operation)
                .map(OperationConfig::getHeaders)
                .orElseGet(HashMap::new)
                .entrySet()
                .forEach(entry -> headers.add(
                        entry.getKey(),
                        translator.parseString(entry.getValue(), request)));
    }
}

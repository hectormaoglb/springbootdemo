package com.example.demo.cfg;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

@Getter
@Setter
public class EndpointConfig {
    private String path;
    private HttpMethod method;
}

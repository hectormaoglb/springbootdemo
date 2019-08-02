package com.example.demo.cfg;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class OperationConfig {
    private String url;
    private HttpMethod method;
    private String payload;
    private boolean applyTemplate;
    private Map<String, String> headers;
    private String templatePath;
    private List<Integer> validStatusCodes;
}

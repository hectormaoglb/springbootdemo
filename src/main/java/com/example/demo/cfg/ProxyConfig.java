package com.example.demo.cfg;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "service.proxy")
@Getter
@Setter
public class ProxyConfig {
    private Map<String, OperationConfig> operations;

}

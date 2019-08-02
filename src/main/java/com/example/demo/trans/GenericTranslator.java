package com.example.demo.trans;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class GenericTranslator {

    private SpringTemplateEngine templateEngine;

    private ObjectMapper mapper;

    @Autowired
    public GenericTranslator(@Qualifier("messageTemplateEngine") SpringTemplateEngine templateEngine){
        this.templateEngine = templateEngine;
        mapper = new ObjectMapper();
    }

    public String parseString(String unparsedURL, Map<String, Object> values){
        StrSubstitutor substitutor = new StrSubstitutor(values);
        return substitutor.replace(unparsedURL);
    }

    public Map parseJson(String unparsedURL, Map<String, Object> values){
        try {
            String parsedStr = parseString(unparsedURL, values);
            return mapper.readValue(parsedStr, Map.class);
        } catch(Exception ex){
            log.error("Error parsing json {0}", ex, unparsedURL);
            return new HashMap();
        }
    }

    public Map parseThirdPartyResponse(Map<String, Object> request, Map<String,Object> response, String operationName){
        try {
            Context context = new Context();
            context.setVariable("request", request);
            context.setVariable("response", response);
            String parsedResponse = templateEngine.process("json/" + operationName, context);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(parsedResponse,new TypeReference<Map<String,Object>>(){});
        } catch(Exception ex){
            log.warn("Error parsing response: {0}",ex.getMessage(), ex);
            return new HashMap<>();
        }
    }

}

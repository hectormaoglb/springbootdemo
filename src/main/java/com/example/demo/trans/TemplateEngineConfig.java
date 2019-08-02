package com.example.demo.trans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

import java.util.Collection;
import java.util.Collections;

@Configuration
public class TemplateEngineConfig {

    public static final String TEMPLATES_BASE = "classpath:/templates/";
    public static final String XML_TEMPLATES_RESOLVE_PATTERN = "json/*";

    @Bean
    public SpringResourceTemplateResolver classpathTemplateResolver() {
        SpringResourceTemplateResolver theResourceTemplateResolver =
                new SpringResourceTemplateResolver();
        theResourceTemplateResolver.setPrefix(TEMPLATES_BASE);
        theResourceTemplateResolver.setResolvablePatterns(
                Collections.singleton(XML_TEMPLATES_RESOLVE_PATTERN));
        theResourceTemplateResolver.setSuffix(".json");
        theResourceTemplateResolver.setCharacterEncoding("UTF-8");
        theResourceTemplateResolver.setCacheable(true);
        theResourceTemplateResolver.setOrder(2);
        return theResourceTemplateResolver;
    }

    /**
     * Creates the template engine for all message templates.
     *
     * @param inTemplateResolvers Template resolver for different types of messages etc.
     * Note that any template resolvers defined elsewhere will also be included in this
     * collection.
     * @return Template engine.
     */
    @Bean
    public SpringTemplateEngine messageTemplateEngine(
            final Collection<SpringResourceTemplateResolver> inTemplateResolvers) {
        final SpringTemplateEngine theTemplateEngine = new SpringTemplateEngine();
        for (SpringResourceTemplateResolver theTemplateResolver : inTemplateResolvers) {
            theTemplateEngine.addTemplateResolver(theTemplateResolver);
        }
        return theTemplateEngine;
    }

}

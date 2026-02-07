package com.geovannycode.payments.api.infra.config;

import io.vavr.jackson.datatype.VavrModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public VavrModule vavrModule() {
        return new VavrModule();
    }
}

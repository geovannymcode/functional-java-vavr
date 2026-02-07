package com.geovannycode.payments.api.infra.config;

import com.geovannycode.payments.api.app.PaymentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public PaymentService paymentAppService() {
        return new PaymentService();
    }
}

package com.geovannycode.payments.api.config;

import com.geovannycode.payments.api.service.PaymentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public PaymentService paymentAppService() {
        return new PaymentService();
    }
}

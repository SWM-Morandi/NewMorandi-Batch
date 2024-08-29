package kr.co.morandi_batch.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class WebClientTestConfig {

    @Bean
    @Primary
    WebClient testWebClient(ExchangeFunction exchangeFunction) {
        return WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();
    }
    
    @Bean
    ExchangeFunction exchangeFunction() {
        return mock(ExchangeFunction.class);
    }
}

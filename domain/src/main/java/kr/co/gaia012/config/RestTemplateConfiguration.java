package kr.co.gaia012.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class RestTemplateConfiguration {

    @Bean
    RestTemplate restTemplate() {
        ClientHttpRequestInterceptor interceptor = (HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {
            log.info(String.format("request to URI %s with HTTP verb '%s'", request.getURI(), request.getMethod().toString()));
            return execution.execute(request, body);
        };
        return new RestTemplateBuilder().additionalInterceptors(interceptor).build();
    }
}

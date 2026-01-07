package com.datn.viettel.configs;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = createHttpComponentsFactory();
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getInterceptors().add((request, body, execution) -> {
            long startTime = System.currentTimeMillis();
            try {
                var response = execution.execute(request, body);
                long duration = System.currentTimeMillis() - startTime;
                if (duration > 5000) {
                    log.warn("Slow API call to {} took {}ms", request.getURI(), duration);
                }
                return response;
            } catch (Exception e) {
                long duration = System.currentTimeMillis() - startTime;
                log.error("Failed API call to {} after {}ms: {}", request.getURI(), duration, e.getMessage());
                throw e;
            }
        });
        return restTemplate;
    }

    private HttpComponentsClientHttpRequestFactory createHttpComponentsFactory() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(50);
        RequestConfig rc = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(20))
                .setResponseTimeout(Timeout.ofSeconds(60))
                .setCookieSpec(StandardCookieSpec.RELAXED)
                .build();
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(rc)
                .disableCookieManagement()
                .build();
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

}
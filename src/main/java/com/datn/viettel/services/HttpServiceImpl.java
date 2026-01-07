package com.datn.viettel.services;


import com.datn.viettel.repositories.core.HttpLogRepository;
import com.datn.viettel.services.iservice.HttpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class HttpServiceImpl implements HttpService {

    private final RestTemplate restTemplate;
    private final HttpLogRepository httpLogRepository;

    @Autowired
    public HttpServiceImpl(RestTemplate restTemplate, HttpLogRepository httpLogRepository) {
        this.restTemplate = restTemplate;
        this.httpLogRepository = httpLogRepository;
    }

    @Override
    public <T> T get(String url, Class<T> responseType, HttpHeaders headers) {
        return exchangeWithLogging(url, HttpMethod.GET, null, responseType, headers, "GET").getBody();
    }

    @Override
    public <T> ResponseEntity<T> getEntity(String url, Class<T> responseType, HttpHeaders headers) {
        return exchangeWithLogging(url, HttpMethod.GET, null, responseType, headers, "GET entity");
    }

    @Override
    public <T> T post(String url, Object requestBody, Class<T> responseType, HttpHeaders headers) {
        return exchangeWithLogging(url, HttpMethod.POST, requestBody, responseType, headers, "POST").getBody();
    }

    @Override
    public <T> ResponseEntity<T> postEntity(String url, Object requestBody, Class<T> responseType, HttpHeaders headers) {
        return exchangeWithLogging(url, HttpMethod.POST, requestBody, responseType, headers, "POST entity");
    }

    @Override
    public <T> T put(String url, Object requestBody, Class<T> responseType, HttpHeaders headers) {
        return exchangeWithLogging(url, HttpMethod.PUT, requestBody, responseType, headers, "PUT").getBody();
    }

    @Override
    public <T> ResponseEntity<T> putEntity(String url, Object requestBody, Class<T> responseType, HttpHeaders headers) {
        return exchangeWithLogging(url, HttpMethod.PUT, requestBody, responseType, headers, "PUT entity");
    }

    @Override
    public <T> T patch(String url, Object requestBody, Class<T> responseType, HttpHeaders headers) {
        return exchangeWithLogging(url, HttpMethod.PATCH, requestBody, responseType, headers, "PATCH").getBody();
    }

    @Override
    public <T> ResponseEntity<T> patchEntity(String url, Object requestBody, Class<T> responseType, HttpHeaders headers) {
        return exchangeWithLogging(url, HttpMethod.PATCH, requestBody, responseType, headers, "PATCH entity");
    }

    @Override
    public <T> T delete(String url, Class<T> responseType, HttpHeaders headers) {
        return exchangeWithLogging(url, HttpMethod.DELETE, null, responseType, headers, "DELETE").getBody();
    }

    @Override
    public <T> ResponseEntity<T> deleteEntity(String url, Class<T> responseType, HttpHeaders headers) {
        return exchangeWithLogging(url, HttpMethod.DELETE, null, responseType, headers, "DELETE entity");
    }

    @Override
    public String callSoap(String url, String rawSoapRequestXml) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        HttpEntity<String> request = new HttpEntity<>(rawSoapRequestXml, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );
        return response.getBody();
    }

    private <T> HttpEntity<T> buildHttpEntity(T body, HttpHeaders headers) {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }
        String language = LocaleContextHolder.getLocale().getLanguage();
        List<Locale.LanguageRange> languageRanges = Locale.LanguageRange.parse(language);
        headers.setAcceptLanguage(languageRanges);
        return new HttpEntity<>(body, headers);
    }

    private <T> ResponseEntity<T> exchangeWithLogging(
            String url,
            HttpMethod method,
            Object requestBody,
            Class<T> responseType,
            HttpHeaders headers,
            String description
    ) {
        LocalDateTime requestAt = LocalDateTime.now();
        HttpEntity<?> entity = buildHttpEntity(requestBody, headers);
        LocalDateTime responseAt = null;

        try {
            ResponseEntity<T> response = restTemplate.exchange(url, method, entity, responseType);
            responseAt = LocalDateTime.now();
            httpLogRepository.logHttpRequest(
                    url,
                    method.name(),
                    entity.getHeaders(),
                    requestBody,
                    response.getBody(),
                    response.getStatusCode().value(),
                    requestAt,
                    responseAt,
                    description
            );
            return response;

        } catch (HttpStatusCodeException ex) {
            responseAt = LocalDateTime.now();
            httpLogRepository.logHttpRequest(
                    url,
                    method.name(),
                    entity.getHeaders(),
                    requestBody,
                    ex.getResponseBodyAsString(),
                    ex.getStatusCode().value(),
                    requestAt,
                    responseAt,
                    description
            );
            throw ex;
        } catch (Exception ex) {
            responseAt = LocalDateTime.now();
            httpLogRepository.logHttpRequest(
                    url,
                    method.name(),
                    entity.getHeaders(),
                    requestBody,
                    ex.getMessage(),
                    0,
                    requestAt,
                    responseAt,
                    description
            );
            throw ex;
        }
    }

}

package com.datn.viettel.configs;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.elasticsearch.client.RestClient;
import org.apache.http.HttpHost;

import java.net.URI;
import java.util.Arrays;


@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris}")
    private String uris;

    @Bean
    public RestClient restClientElasticsearch() {
        return RestClient.builder(buildHttpHosts()).build();
    }

    @Bean
    public ElasticsearchTransport elasticsearchTransport(RestClient restClient) {
        return new RestClientTransport(
                restClient,
                new JacksonJsonpMapper()
        );
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(ElasticsearchTransport transport) {
        return new ElasticsearchClient(transport);
    }

    private HttpHost[] buildHttpHosts() {
        return Arrays.stream(uris.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(this::toHttpHost)
                .toArray(HttpHost[]::new);
    }

    private HttpHost toHttpHost(String u) {
        URI uri = URI.create(u);
        return new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
    }
}

package com.datn.viettel;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EsHealthRunner implements CommandLineRunner {
    private final ElasticsearchClient client;

    @Override
    public void run(String... args) throws Exception {
        var info = client.info();
        System.out.println("✅ ES connected. cluster=" + info.clusterName() + ", version=" + info.version().number());
    }
}

package org.silverstar.partnerelasticsearch.index;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class PartnerIndexInitializer {

    private final ElasticsearchClient elasticsearchClient;

    @PostConstruct
    public void createPartnerIndexIfNotExists() {
        try {
            boolean exists = elasticsearchClient.indices()
                    .exists(ExistsRequest.of(e -> e.index("partner"))).value();

            if (!exists) {
                elasticsearchClient.indices().create(CreateIndexRequest.of(c -> c
                        .index("partner")
                        .mappings(m -> m
                            .properties("partnerId", p -> p.keyword(k -> k))
                            .properties("timestamp", p -> p.date(d -> d))
                            .properties("info", p -> p.object(o -> o
                                .properties("owner_name", pp -> pp.text(t -> t))
                                .properties("status", pp -> pp.keyword(k -> k))
                            ))
                        )
                ));

                insertPartnerIndexData();

                log.info("Created partner index");
            } else {
                log.info("Index already exists");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("ElasticSearch 인덱스 생성 실패", e);
        }
    }

    private void insertPartnerIndexData() throws IOException {
        // 문서 1
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("partnerId", "P001");
        Map<String, Object> info1 = new HashMap<>();
        info1.put("owner_name", "홍길동");
        info1.put("status", "ACTIVE");
        doc1.put("info", info1);
        doc1.put("timestamp", "2025-09-26T12:00:00Z");

        elasticsearchClient.index(i -> i.index("partner-index").id("1").document(doc1));

        // 문서 2
        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("partnerId", "P002");
        Map<String, Object> info2 = new HashMap<>();
        info2.put("owner_name", "이순신");
        info2.put("status", "INACTIVE");
        doc2.put("info", info2);
        doc2.put("timestamp", "2025-09-26T13:00:00Z");

        elasticsearchClient.index(i -> i.index("partner-index").id("2").document(doc2));

        // 문서 3
        Map<String, Object> doc3 = new HashMap<>();
        doc3.put("partnerId", "P003");
        Map<String, Object> info3 = new HashMap<>();
        info3.put("owner_name", "강감찬");
        info3.put("status", "ACTIVE");
        doc3.put("info", info3);
        doc3.put("timestamp", "2025-09-26T14:00:00Z");

        elasticsearchClient.index(i -> i.index("partner-index").id("3").document(doc3));

        // 문서 4
        Map<String, Object> doc4 = new HashMap<>();
        doc4.put("partnerId", "P004");
        Map<String, Object> info4 = new HashMap<>();
        info4.put("owner_name", "신사임당");
        info4.put("status", "ACTIVE");
        doc4.put("info", info4);
        doc4.put("timestamp", "2025-09-26T15:00:00Z");

        elasticsearchClient.index(i -> i.index("partner-index").id("4").document(doc4));

        // 문서 5
        Map<String, Object> doc5 = new HashMap<>();
        doc5.put("partnerId", "P005");
        Map<String, Object> info5 = new HashMap<>();
        info5.put("owner_name", "세종대왕");
        info5.put("status", "INACTIVE");
        doc5.put("info", info5);
        doc5.put("timestamp", "2025-09-26T16:00:00Z");

        elasticsearchClient.index(i -> i.index("partner-index").id("5").document(doc5));

    }
}

package org.silverstar.partnerelasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;
import org.silverstar.partnerelasticsearch.domain.Partner;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerIndexQueryService {

    private final ElasticsearchClient client;

    // 검색 (match query)
    public List<Partner> search(String index, String field, String value) throws IOException {
        SearchResponse<Partner> response = client.search(s -> s
                        .index(index)
                        .query(q -> q.match(m -> m.field(field).query(value))),
                Partner.class);

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public List<Partner> searchWithPaging(String index, String field, String value, int page, int size) throws IOException {
        int from = (page - 1) * size; // 0-based offset

        SearchResponse<Partner> response = client.search(s -> s
                        .index(index)
                        .query(q -> q.match(m -> m.field(field).query(value)))
                        .from(from)
                        .size(size),
                Partner.class);

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    // 최초 페이지 조회
    public SearchResponse<Partner> searchFirstPage(String index, int size) throws IOException {
        return client.search(s -> s
                        .index(index)
                        .size(size)
                        .sort(sort -> sort.field(f -> f.field("partnerId.keyword").order(SortOrder.Asc))),
                Partner.class);
    }

    // search_after로 다음 페이지 조회
    public SearchResponse<Partner> searchNextPage(String index, int size, List<FieldValue> searchAfterValues) throws IOException {
        return client.search(s -> s
                        .index(index)
                        .size(size)
                        .sort(sort -> sort.field(f -> f.field("partnerId.keyword").order(SortOrder.Asc)))
                        .searchAfter(searchAfterValues),
                Partner.class);
    }

    public List<String> findUniquePartnerIdsAfterDate(String index, LocalDateTime dateTime) throws IOException {
        String isoDate = dateTime.toString(); // "2025-09-26T12:00:00"

        SearchResponse<Void> response = client.search(s -> s
                        .index(index)
                        .size(0) // 문서 자체는 불필요, aggregation만
                        .query(q -> q.range(r -> r
                                .field("updatedAt")
                                .gte(JsonData.of(isoDate))
                        ))
                        .aggregations("unique_partnerIds", a -> a
                                .terms(t -> t.field("partnerId.keyword").size(10000))
                        ),
                Void.class);

        return response.aggregations()
                .get("unique_partnerIds")
                .sterms()
                .buckets()
                .array()
                .stream()
                .map(b -> b.key().stringValue())
                .collect(Collectors.toList());
    }

    public List<String> findUniquePartnerIdsAfterDateWithPaging(
            String index, LocalDateTime dateTime, int page, int size) throws IOException {

        String isoDate = dateTime.toString(); // e.g. "2025-09-26T12:00:00"
        int from = (page - 1) * size; // pagination offset

        SearchResponse<Void> response = client.search(s -> s
                        .index(index)
                        .size(0) // document hit은 필요 없음
                        .query(q -> q.range(r -> r
                                .field("updatedAt")
                                .gte(JsonData.of(isoDate))
                        ))
                        .aggregations("unique_partnerIds", a -> a
                                .terms(t -> t
                                        .field("partnerId.keyword")
                                        .size(from + size) // 요청한 page까지 확보
                                )
                        ),
                Void.class);

        // unique partnerId bucket 목록
        List<String> allIds = response.aggregations()
                .get("unique_partnerIds")
                .sterms()
                .buckets()
                .array()
                .stream()
                .map(b -> b.key().stringValue())
                .collect(Collectors.toList());

        // 페이지네이션 처리
        return allIds.stream()
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }


}

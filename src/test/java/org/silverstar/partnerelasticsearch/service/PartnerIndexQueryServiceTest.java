package org.silverstar.partnerelasticsearch.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.silverstar.partnerelasticsearch.domain.Partner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Import({PartnerIndexQueryService.class})
class PartnerIndexQueryServiceTest extends ElasticSearchContainerTest {

    @Autowired
    private PartnerIndexQueryService partnerQueryService;

    static private final String INDEX_NAME = "partner-index";
    static private final int DOCUMENT_COUNT = 5;

    // 초기 데이터 설정
    @BeforeAll
    static void beforeAll() throws IOException {
        for (int i = 1; i <= DOCUMENT_COUNT; i++) {
            Map<String, Object> document = Map.of(
                    "partnerId", "P" + i,
                    "ownerName", "홍길동",
                    "status", "NORMAL",
                    "updatedAt", LocalDateTime.now().toString()
            );
            IndexRequest<Map<String, Object>> request = IndexRequest.of(j -> j
                    .index(INDEX_NAME)
                    .document(document));
            client.index(request);
        }

        // 인덱싱 refresh
        client.indices().refresh(r -> r.index(INDEX_NAME));

        // 전체 문서 검색 (match_all)
        SearchResponse<Partner> response = client.search(s -> s
                        .index(INDEX_NAME)
                        .query(q -> q.matchAll(m -> m)),
                Partner.class);

        int totalDocs = response.hits().hits().size();

        assertEquals(DOCUMENT_COUNT, totalDocs);
    }

    @Test
    void testFindUniquePartnerIdsAfterDate() throws IOException {
        List<Partner> partners = partnerQueryService.search(INDEX_NAME,
                "ownerName", "홍길동");

        assertEquals(DOCUMENT_COUNT, partners.size());
    }

    @Test
    void testCountUniquePartnerIdsAfterDate() throws IOException {
        LocalDateTime dateTime = LocalDateTime.of(2025, 9, 26, 12, 0);

        List<String> partners = partnerQueryService.findUniquePartnerIdsAfterDate(INDEX_NAME,
                dateTime);

        assertEquals(DOCUMENT_COUNT, partners.size());
    }

    @Test
    void testCountUniquePartnerIdsAfterDate_no_data() throws IOException {
        LocalDateTime dateTime = LocalDateTime.now();

        List<String> partners = partnerQueryService.findUniquePartnerIdsAfterDate(INDEX_NAME,
                dateTime);

        assertEquals(0, partners.size());
    }


    @Test
    void testSearchWithPaging() throws IOException {
        String field = "ownerName";
        String value = "홍길동";
        int pageSize = 3;

        // 1페이지 조회
        List<Partner> firstPage = partnerQueryService.searchWithPaging(INDEX_NAME, field, value, 1, pageSize);

        assertFalse(firstPage.isEmpty());
        assertTrue(firstPage.size() <= pageSize);

        // 2페이지 조회
        List<Partner> secondPage = partnerQueryService.searchWithPaging(INDEX_NAME, field, value, 2, pageSize);

        assertFalse(secondPage.isEmpty());
        assertTrue(secondPage.size() <= pageSize);

        // 총 개수 확인
        int totalFetched = firstPage.size() + secondPage.size();
        assertTrue(totalFetched <= DOCUMENT_COUNT);
    }

    @Test
    void testSearchAfterPaging() throws IOException {
        int size = 3;

        // 첫 번째 페이지 요청
        SearchResponse<Partner> firstPage = partnerQueryService.searchFirstPage(INDEX_NAME, size);
        List<Hit<Partner>> hits1 = firstPage.hits().hits();
        assertEquals(size, hits1.size());

        // search_after 값 가져오기 (마지막 문서의 정렬 기준)
        List<FieldValue> searchAfter = hits1.get(hits1.size() - 1).sort();

        // 두 번째 페이지 요청
        SearchResponse<Partner> secondPage = partnerQueryService.searchNextPage(INDEX_NAME, size, searchAfter);
        List<Hit<Partner>> hits2 = secondPage.hits().hits();
        assertEquals(2, hits2.size());

        assertFalse(hits1.isEmpty());
        assertFalse(hits2.isEmpty());
    }

}

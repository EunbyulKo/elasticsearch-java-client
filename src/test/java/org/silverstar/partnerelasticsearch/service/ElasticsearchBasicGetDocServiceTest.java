package org.silverstar.partnerelasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ElasticsearchBasicGetDocServiceTest {

    @Mock
    private ElasticsearchClient client;

    @InjectMocks
    private ElasticsearchBasicService elasticSearchBasicService;

    @Test
    void testGetDocument_found() throws Exception {
        // given
        String index = "partner-index";
        String id = "1";

        Map<String, Object> mockSource = Map.of(
                "partnerId", "P001",
                "ownerName", "홍길동"
        );

        // mock 생성
        GetResponse<Map> mockResponse = mock(GetResponse.class);
        when(mockResponse.found()).thenReturn(true);
        when(mockResponse.source()).thenReturn(mockSource);
        when(client.get(any(Function.class), eq(Map.class))).thenReturn(mockResponse);

        // when
        Map<String, Object> result = elasticSearchBasicService.getDocument(index, id);

        // then
        assertNotNull(result);
        assertEquals("P001", result.get("partnerId"));
        assertEquals("홍길동", result.get("ownerName"));
    }

    @Test
    void testGetDocument_notFound() throws Exception {
        // given
        String index = "partner-index";
        String id = "999";

        // mock 생성
        GetResponse<Map> mockResponse = mock(GetResponse.class);
        when(mockResponse.found()).thenReturn(false);
        when(client.get(any(Function.class), eq(Map.class))).thenReturn(mockResponse);

        // when
        Map<String, Object> result = elasticSearchBasicService.getDocument(index, id);

        // then
        assertNull(result);
    }

    @Test
    void testGetDocument_throwIOException() throws Exception {
        // given
        when(client.get(any(Function.class), eq(Map.class)))
                .thenThrow(new IOException("Elasticsearch 연결 오류"));

        // then
        assertThrows(IOException.class, () ->
                elasticSearchBasicService.getDocument("partner-index", "1")
        );
    }

}
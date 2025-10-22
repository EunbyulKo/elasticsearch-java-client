package org.silverstar.partnerelasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElasticsearchBasicSetDocServiceTest {

    @Mock
    private ElasticsearchClient client;

    @InjectMocks
    private ElasticsearchBasicService elasticSearchBasicService;

    @Test
    void testAddDocument() throws Exception {
        // given
        String index = "partner-index";
        Map<String, Object> document = Map.of(
                "partnerId", "P001",
                "ownerName", "홍길동"
        );

        // mock 생성
        IndexResponse mockResponse = mock(IndexResponse.class);
        when(mockResponse.id()).thenReturn("generated-id-123");
        when(client.index(any(IndexRequest.class))).thenReturn(mockResponse);

        // when
        String resultId = elasticSearchBasicService.addDocument(index, document);

        // then
        assertNotNull(resultId);
        assertEquals("generated-id-123", resultId);
    }

    @Test
    void testUpdateDocument() throws Exception {
        // given
        String index = "partner-index";
        String id = "123";
        Map<String, Object> document = Map.of(
                "partnerId", "P001",
                "status", "UPDATED"
        );

        // mock 생성
        IndexResponse mockResponse = mock(IndexResponse.class);
        when(mockResponse.id()).thenReturn(id);
        when(client.index(any(IndexRequest.class))).thenReturn(mockResponse);

        // when
        String result = elasticSearchBasicService.updateDocument(index, id, document);

        // then
        assertNotNull(result);
        assertEquals(id, result);
    }



}
package org.silverstar.partnerelasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ElasticsearchBasicService {

    private final ElasticsearchClient client;

    // 문서 추가
    public String addDocument(String index, Map<String, Object> document) throws IOException {
        IndexRequest<Map<String, Object>> request = IndexRequest.of(i -> i
                .index(index)
                .document(document));
        IndexResponse response = client.index(request);
        return response.id();
    }

    // 문서 수정
    public String updateDocument(String index, String id, Map<String, Object> document) throws IOException {
        IndexRequest<Map<String, Object>> request = IndexRequest.of(i -> i
                .index(index)
                .id(id)
                .document(document));
        IndexResponse response = client.index(request);
        return response.id();
    }

    // 문서 조회
    public Map<String, Object> getDocument(String index, String id) throws IOException {
        GetResponse<Map> response = client.get(g -> g.index(index).id(id), Map.class);
        if (response.found()) {
            return response.source();
        } else {
            return null;
        }
    }

    // 문서 삭제
    public boolean deleteDocument(String index, String id) throws IOException {
        DeleteResponse response = client.delete(d -> d.index(index).id(id));
        return response.result().name().equals("Deleted");
    }

}

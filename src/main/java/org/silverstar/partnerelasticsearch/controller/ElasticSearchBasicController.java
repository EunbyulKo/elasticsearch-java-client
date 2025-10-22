package org.silverstar.partnerelasticsearch.controller;

import lombok.RequiredArgsConstructor;
import org.silverstar.partnerelasticsearch.service.ElasticsearchBasicService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/elasticsearch")
@RequiredArgsConstructor
public class ElasticSearchBasicController {

    private final ElasticsearchBasicService elasticsearchBasicService;

    // 문서 추가/수정
    @PostMapping("/{index}/")
    public String addDocument(@PathVariable String index,
                                      @RequestBody Map<String, Object> document) throws IOException {
        return elasticsearchBasicService.addDocument(index, document);
    }

    @PutMapping("/{index}/{id}")
    public String updateDocument(@PathVariable String index,
                                      @PathVariable String id,
                                      @RequestBody Map<String, Object> document) throws IOException {
        return elasticsearchBasicService.updateDocument(index, id, document);
    }

    // 문서 조회
    @GetMapping("/{index}/{id}")
    public Map<String, Object> getDocument(@PathVariable String index,
                                           @PathVariable String id) throws IOException {
        return elasticsearchBasicService.getDocument(index, id);
    }

    // 문서 삭제
    @DeleteMapping("/{index}/{id}")
    public boolean deleteDocument(@PathVariable String index,
                                  @PathVariable String id) throws IOException {
        return elasticsearchBasicService.deleteDocument(index, id);
    }

}

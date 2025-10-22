package org.silverstar.partnerelasticsearch.controller;

import org.silverstar.partnerelasticsearch.domain.Partner;
import org.silverstar.partnerelasticsearch.service.PartnerIndexQueryService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/elasticsearch")
public class PartnerIndexQueryController {

    private final PartnerIndexQueryService partnerQueryService;

    public PartnerIndexQueryController(PartnerIndexQueryService partnerQueryService) {
        this.partnerQueryService = partnerQueryService;
    }

    // 검색
    @GetMapping("/{index}/search")
    public List<Partner> search(@PathVariable String index,
                                @RequestParam String field,
                                @RequestParam String value) throws IOException {
        return partnerQueryService.search(index, field, value);
    }
}

package org.silverstar.partnerelasticsearch.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Partner {
    private String partnerId;
    private String ownerName;
    private String status;
    private String updatedAt;
}

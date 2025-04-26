package com.scoring.test_task.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;
import java.util.List;

@Document(indexName = "scoring-result")
@Data
@Builder
public class ScoringResult {

    @Id
    private String id;

    private String orgName;
    private Boolean isRejected;
    private List<String> rejectReasons;
}

package com.scoring.test_task.repository;

import com.scoring.test_task.model.ScoringResult;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoringRepository extends ElasticsearchRepository<ScoringResult, String> {

    List<ScoringResult> findByOrgName(String orgName);
}

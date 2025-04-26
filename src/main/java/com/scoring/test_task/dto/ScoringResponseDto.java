package com.scoring.test_task.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ScoringResponseDto {
    private String orgName;
    private boolean rejected;
    private List<String> rejectReasons;
}

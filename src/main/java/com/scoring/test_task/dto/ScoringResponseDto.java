package com.scoring.test_task.dto;

import lombok.Data;

import java.util.List;

@Data
public class ScoringResponseDto {
    private String orgName;
    private boolean rejected;
    private List<String> rejectReasons;
}

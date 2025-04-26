package com.scoring.test_task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ScoringResponseDto {

    @Schema(description = "Organization full name")
    private String orgName;

    @Schema(description = "Scoring result: true if rejected, false if passed")
    private Boolean isRejected;

    @Schema(description = "List of reject reasons if scoring failed")
    private List<String> rejectReasons;
}

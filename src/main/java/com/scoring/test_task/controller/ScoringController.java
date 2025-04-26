package com.scoring.test_task.controller;

import com.scoring.test_task.dto.ScoringRequestDto;
import com.scoring.test_task.dto.ScoringResponseDto;
import com.scoring.test_task.service.ScoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/scoring")
@Tag(name = "Organization scoring", description = "Automated scoring with DMN by Camunda")
public class ScoringController {
    public final ScoringService scoringService;

    @PostMapping
    @Operation(summary = "Get organization score",
            description = "Submit organization info in JSON format to calculate and retrieve scoring result.")
    public ResponseEntity<ScoringResponseDto> scoreOrg(@Valid @RequestBody ScoringRequestDto requestDto) {
        log.info("Requesting score for '{}'", requestDto);
        return ResponseEntity.ok(scoringService.scoreOrg(requestDto));
    }

    @GetMapping("/{orgName}")
    @Operation(summary = "Find organization scoring in the database",
            description = "Provide organization name to retrieve scoring results from the database.")
    public ResponseEntity<List<ScoringResponseDto>> findOrgScoring(
            @PathVariable @NotBlank(message = "Organization name must not be empty or null.") String orgName) {
        log.info("Searching scoring for '{}' in db.", orgName);
        return ResponseEntity.ok(scoringService.findScoringResultByOrgName(orgName));
    }
}

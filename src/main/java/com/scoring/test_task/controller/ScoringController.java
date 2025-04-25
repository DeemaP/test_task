package com.scoring.test_task.controller;

import com.scoring.test_task.dto.ScoringRequestDto;
import com.scoring.test_task.dto.ScoringResponseDto;
import com.scoring.test_task.service.ScoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/scoring")
@Tag(name = "Organization scoring", description = "Automated  scoring with DMN by Camunda")
public class ScoringController {
    public final ScoringService scoringService;

    @PostMapping
    @Operation(summary = "Get organization score.", description = "Put organization info JSON to get score.")
    public ResponseEntity<ScoringResponseDto> getCompanyScore(@Valid @RequestBody ScoringRequestDto requestDto) {
        log.info("Requesting score for '{}'", requestDto);
        return ResponseEntity.ok(scoringService.scoreOrg(requestDto));
    }
}

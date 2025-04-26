package com.scoring.test_task.service;

import com.scoring.test_task.config.ScoringVariables;
import com.scoring.test_task.dto.ScoringRequestDto;
import com.scoring.test_task.dto.ScoringResponseDto;
import com.scoring.test_task.model.ScoringResult;
import com.scoring.test_task.repository.ScoringRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoringService {
    private final DecisionService decisionService;
    private final ScoringVariables properties;
    private final ScoringRepository scoringRepository;

    public ScoringResponseDto scoreOrg(ScoringRequestDto requestDto) {
        String orgName = requestDto.getOrgName();
        log.info("Start scoring for organization '{}'", orgName);
        DmnDecisionTableResult dmnResult = decisionService.evaluateDecisionTableByKey(
                properties.getDmnKey(), createDmnInput(requestDto));
        ScoringResponseDto responseDto = createScoringResponseDto(dmnResult, orgName);
        saveScoringResult(responseDto);
        log.info("Finished scoring for '{}'", orgName);

        return responseDto;
    }

    public List<ScoringResponseDto> findScoringResultByOrgName(String orgName) {
        log.info("Searching all scoring results in db for '{}'", orgName);

        List<ScoringResponseDto> result = scoringRepository.findByOrgName(orgName).stream()
                .map(this::mapScoringResultToResponse)
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            log.debug("No scoring results found for '{}' in db.", orgName);
            throw new EntityNotFoundException(String.format("No scoring results found for %s in db.", orgName));
        }

        return result;
    }

    private Map<String, Object> createDmnInput(ScoringRequestDto requestDto) {
        log.debug("Preparing DMN input variables for '{}'", requestDto.getOrgName());
        Map<String, Object> dmnInput = new HashMap<>();
        String inn = requestDto.getInn();

        dmnInput.put(properties.getIsIpKey(), isIp(inn));
        dmnInput.put(properties.getIsResidentKey(), isResident(inn));
        dmnInput.put(properties.getRegionKey(), requestDto.getRegion());
        dmnInput.put(properties.getCapitalKey(), requestDto.getCapital());

        return dmnInput;
    }

    private ScoringResponseDto createScoringResponseDto(DmnDecisionTableResult dmnResult, String orgName) {
        log.debug("Building scoring response for '{}'", orgName);
        List<Map<String, Object>> results = dmnResult.getResultList();
        List<String> rejectReasons = new ArrayList<>();

        boolean isRejected = results.stream()
                .anyMatch(result -> result.get(properties.getRejectedKey()).equals(true));

        if (isRejected) {
            rejectReasons = results.stream()
                    .map(result -> (String) result.get(properties.getReasonKey()))
                    .collect(Collectors.toList());
        }

        return ScoringResponseDto.builder()
                .orgName(orgName)
                .isRejected(isRejected)
                .rejectReasons(rejectReasons)
                .build();
    }

    private void saveScoringResult(ScoringResponseDto responseDto) {
        String orgName = responseDto.getOrgName();
        log.debug("Saving scoring result in db for '{}'", orgName);
        scoringRepository.save(ScoringResult.builder()
                .orgName(orgName)
                .isRejected(responseDto.getIsRejected())
                .rejectReasons(responseDto.getRejectReasons())
                .build());
    }

    private boolean isIp(String inn) {
        return inn.length() == properties.getIpInnLength();
    }

    private boolean isResident(String inn) {
        return !inn.startsWith(properties.getNonResidentPrefix());
    }

    private ScoringResponseDto mapScoringResultToResponse(ScoringResult scoringResult) {
        return ScoringResponseDto.builder()
                .orgName(scoringResult.getOrgName())
                .isRejected(scoringResult.getIsRejected())
                .rejectReasons(scoringResult.getRejectReasons())
                .build();
    }
}

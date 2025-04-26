package com.scoring.test_task.service;

import com.scoring.test_task.config.ScoringVariables;
import com.scoring.test_task.dto.ScoringRequestDto;
import com.scoring.test_task.dto.ScoringResponseDto;
import com.scoring.test_task.model.ScoringResult;
import com.scoring.test_task.repository.ScoringRepository;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoringServiceTest {

    @Mock
    private DecisionService decisionService;

    @Mock
    private ScoringVariables properties;

    @Mock
    private ScoringRepository scoringRepository;

    @Mock
    private DmnDecisionTableResult dmnResult;

    @InjectMocks
    private ScoringService scoringService;

    private String orgName;
    private BigDecimal capital;
    private ScoringRequestDto validOrgRequest;
    private ScoringResponseDto validOrgResponse;
    private ScoringRequestDto invalidOrgRequest;
    private ScoringResponseDto invalidOrgResponse;
    private List<Map<String, Object>> dmnResultList;

    @BeforeEach
    void setUp() {
        orgName = "OOO COMPANY";
        capital = new BigDecimal("5000000");

        validOrgRequest = ScoringRequestDto.builder()
                .orgName(orgName)
                .inn("7707083893")
                .region(77)
                .capital(capital)
                .build();

        validOrgResponse = ScoringResponseDto.builder()
                .orgName(orgName)
                .isRejected(false)
                .rejectReasons(Collections.emptyList())
                .build();

        invalidOrgRequest = ScoringRequestDto.builder()
                .orgName(orgName)
                .inn("770708389312")
                .region(77)
                .capital(capital)
                .build();

        invalidOrgResponse = ScoringResponseDto.builder()
                .orgName(orgName)
                .isRejected(true)
                .rejectReasons(List.of("any reason"))
                .build();
    }

    @Test
    void testScoreOrgSuccess_ValidOrg() {
        dmnResultList = createValidOrgDmnResultList();
        mockProperties();
        when(decisionService.evaluateDecisionTableByKey(any(), any())).thenReturn(dmnResult);
        when(dmnResult.getResultList()).thenReturn(dmnResultList);

        ScoringResponseDto response = scoringService.scoreOrg(validOrgRequest);

        verify(decisionService, times(1)).evaluateDecisionTableByKey(any(), any());
        verify(scoringRepository, times(1)).save(any(ScoringResult.class));
        assertThat(validOrgResponse).isEqualTo(response);
    }

    @Test
    void testScoreOrgSuccess_InvalidOrg() {
        dmnResultList = createInvalidOrgDmnResultList();
        mockProperties();
        when(properties.getReasonKey()).thenReturn("reason");
        when(decisionService.evaluateDecisionTableByKey(any(), any())).thenReturn(dmnResult);
        when(dmnResult.getResultList()).thenReturn(dmnResultList);

        ScoringResponseDto response = scoringService.scoreOrg(invalidOrgRequest);

        verify(decisionService, times(1)).evaluateDecisionTableByKey(any(), any());
        verify(scoringRepository, times(1)).save(any(ScoringResult.class));
        assertThat(invalidOrgResponse).isEqualTo(response);
    }

    @Test
    void testFindScoringResultByOrgNameSuccess() {
        ScoringResult scoringResult = ScoringResult.builder()
                .orgName(orgName)
                .isRejected(false)
                .rejectReasons(Collections.emptyList())
                .build();
        when(scoringRepository.findByOrgName(orgName)).thenReturn(List.of(scoringResult));

        List<ScoringResponseDto> results = scoringService.findScoringResultByOrgName(orgName);

        verify(scoringRepository, times(1)).findByOrgName(orgName);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getOrgName()).isEqualTo(orgName);
        assertThat(results.get(0).getIsRejected()).isFalse();
    }

    @Test
    void testFindScoringResultByOrgName_NotFound() {
        when(scoringRepository.findByOrgName(orgName)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> scoringService.findScoringResultByOrgName(orgName))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No scoring results found");
    }

    @Test
    void testIsIpTrue() {
        when(properties.getIpInnLength()).thenReturn(12);

        boolean result = Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(scoringService, "isIp", "770708389300"));
        assertThat(result).isTrue();
    }

    @Test
    void testIsIpFalse() {
        when(properties.getIpInnLength()).thenReturn(12);

        boolean result = Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(scoringService, "isIp", "7707083893"));
        assertThat(result).isFalse();
    }

    @Test
    void testIsResidentTrue() {
        when(properties.getNonResidentPrefix()).thenReturn("9909");

        boolean result = Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(scoringService, "isResident", "7707083893"));
        assertThat(result).isTrue();
    }

    @Test
    void testIsResidentFalse() {
        when(properties.getNonResidentPrefix()).thenReturn("9909");

        boolean result = Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(scoringService, "isResident", "9909123456"));
        assertThat(result).isFalse();
    }

    private List<Map<String, Object>> createValidOrgDmnResultList() {
        Map<String, Object> map = new HashMap<>();
        map.put("isRejected", false);
        map.put("reason", "");

        return List.of(map);
    }

    private List<Map<String, Object>> createInvalidOrgDmnResultList() {
        Map<String, Object> map = new HashMap<>();
        map.put("isRejected", true);
        map.put("reason", "any reason");

        return List.of(map);
    }

    private void mockProperties() {
        when(properties.getDmnKey()).thenReturn("scoring-decision");
        when(properties.getIsIpKey()).thenReturn("isIp");
        when(properties.getIsResidentKey()).thenReturn("isResident");
        when(properties.getIpInnLength()).thenReturn(12);
        when(properties.getNonResidentPrefix()).thenReturn("9909");
        when(properties.getRegionKey()).thenReturn("region");
        when(properties.getCapitalKey()).thenReturn("capital");
        when(properties.getIsRejectedKey()).thenReturn("isRejected");
    }
}

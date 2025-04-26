package com.scoring.test_task.controller;

import com.scoring.test_task.dto.ScoringRequestDto;
import com.scoring.test_task.dto.ScoringResponseDto;
import com.scoring.test_task.exception.GlobalExceptionHandler;
import com.scoring.test_task.service.ScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScoringController.class)
@Import(GlobalExceptionHandler.class)
class ScoringControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ScoringController scoringController;

    @MockBean
    private ScoringService scoringService;

    private String validRequestJson;
    private String invalidRequestJson;
    private ScoringResponseDto validResponse;

    @BeforeEach
    void setUp() {
        validRequestJson = "{"
                + "\"orgName\": \"OOO COMPANY\","
                + "\"inn\": \"1234567890\","
                + "\"region\": 77,"
                + "\"capital\": 5000000"
                + "}";

        validResponse = ScoringResponseDto.builder()
                .orgName("OOO COMPANY")
                .isRejected(false)
                .rejectReasons(List.of())
                .build();
    }

    @Test
    void testScoreOrgSuccess() throws Exception {
        when(scoringService.scoreOrg(any(ScoringRequestDto.class))).thenReturn(validResponse);

        mockMvc.perform(post("/scoring")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orgName").value("OOO COMPANY"))
                .andExpect(jsonPath("$.isRejected").value(false))
                .andExpect(jsonPath("$.rejectReasons").isArray());
    }

    @Test
    void testScoreOrgFailed_EmptyOrgName() throws Exception {
        invalidRequestJson = "{"
                + "\"orgName\": \" \","
                + "\"inn\": \"1234567890\","
                + "\"region\": 77,"
                + "\"capital\": 5000000"
                + "}";

        mockMvc.perform(post("/scoring")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testScoreOrgFailed_InvalidInn() throws Exception {
        invalidRequestJson = "{"
                + "\"orgName\": \"OOO COMPANY\","
                + "\"inn\": \"123\","
                + "\"region\": 77,"
                + "\"capital\": 5000000"
                + "}";

        mockMvc.perform(post("/scoring")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testScoreOrgFailed_InvalidRegion() throws Exception {
        invalidRequestJson = "{"
                + "\"orgName\": \"OOO COMPANY\","
                + "\"inn\": \"1234567890\","
                + "\"region\": 0,"
                + "\"capital\": 5000000"
                + "}";

        mockMvc.perform(post("/scoring")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testScoreOrgFailed_InvalidCapital() throws Exception {
        invalidRequestJson = "{"
                + "\"orgName\": \"OOO COMPANY\","
                + "\"inn\": \"1234567890\","
                + "\"region\": 77,"
                + "\"capital\": 0"
                + "}";

        mockMvc.perform(post("/scoring")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFindOrgScoringSuccess() throws Exception {
        when(scoringService.findScoringResultByOrgName(anyString())).thenReturn(List.of(validResponse));

        mockMvc.perform(get("/scoring/{orgName}", "OOO COMPANY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orgName").value(validResponse.getOrgName()))
                .andExpect(jsonPath("$[0].isRejected").value(validResponse.getIsRejected()));
    }

    @Test
    void testFindOrgScoringFailed_NotFound() throws Exception {
        when(scoringService.findScoringResultByOrgName(anyString()))
                .thenThrow(new EntityNotFoundException("No scoring results found"));

        mockMvc.perform(get("/scoring/{orgName}", "OOO COMPANY"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No scoring results found"));
    }
}
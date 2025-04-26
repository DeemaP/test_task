package com.scoring.test_task;

import com.scoring.test_task.repository.ScoringRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class TestTaskApplicationTests {

    @MockBean
    private ScoringRepository scoringRepository;

    @Test
    void contextLoads() {
    }
}

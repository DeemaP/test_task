package com.scoring.test_task.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "scoring-variables")
@Data
public class ScoringVariablesProperties {
    private String isIpKey;
    private String isResidentKey;
    private String regionKey;
    private String capitalKey;
    private String nonResidentPrefix;
    private int ipInnLength;
}

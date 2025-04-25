package com.scoring.test_task.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class ScoringRequestDto {

    @NotBlank(message = "Organization name must not be empty or null.")
    private String orgName;

    @NotBlank(message = "Organization INN must not be empty or null.")
    @Pattern(regexp = "\\d{10}|\\d{12}", message = "INN must contain 10 or 12 digits.")
    private String inn;

    @Positive(message = "Organization region code must be greater than 0.")
    private int region;

    @NotNull(message = "Organization capital must not be null.")
    @DecimalMin(value = "10000.0", message = "Organization capital must be equal 10000 or greater.")
    private BigDecimal capital;
}

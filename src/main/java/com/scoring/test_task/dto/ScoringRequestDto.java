package com.scoring.test_task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
public class ScoringRequestDto {

    @Schema(description = "Organization full name", example = "OOO roga kopyta")
    @NotBlank(message = "Organization name must not be empty or null.")
    private String orgName;

    @Schema(description = "Organization INN (must be 10 or 12 digits)", example = "1234567890")
    @NotBlank(message = "Organization INN must not be empty or null.")
    @Pattern(regexp = "\\d{10}|\\d{12}", message = "INN must contain 10 or 12 digits.")
    private String inn;

    @Schema(description = "Organization region code (greater than 0)", example = "77")
    @Min(value = 1, message = "Organization region code must be greater than 0.")
    private int region;

    @Schema(description = "Organization authorized capital, minimum 10000", example = "5000000.00")
    @NotNull(message = "Organization capital must not be null.")
    @DecimalMin(value = "10000.0", message = "Organization capital must be greater than or equal to 10000.")
    private BigDecimal capital;
}

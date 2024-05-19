package org.example.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransferRequest {
    @NotBlank
    @Schema(description = "Счёт, на который будет произведён перевод", example = "admin2")
    private String targetUsername;
    @Min(0)
    @Positive
    @NotNull
    @Schema(description = "Сумма перевода", example = "100")
    private BigDecimal amount;
}

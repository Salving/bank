package org.example.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @Schema(description = "Логин", example = "admin")
    @Size(min = 5, max = 20, message = "Логин должен быть длиной от 5 до 20 символов")
    @NotBlank
    private String username;

    @Schema(description = "ФИО", example = "Васильев Алексей Сергеевич")
    @Size(min = 10, max = 100, message = "ФИО должно быть длиной от 10 до 100 символов")
    @NotBlank
    private String fullName;

    @Schema(description = "Дата рождения", example = "1990-08-26")
    @NotNull
    private LocalDate birthDate;

    @Schema(description = "Список адресов электронной почты", example = "[\"email@fastmail.etc\"]")
    @NotEmpty
    private List<@Email String> emails;

    @Schema(description = "Список телефонных номеров", example = "[\"9217237285\"]")
    @NotEmpty
    private List<@Size(min = 10, max = 10, message = "Телефон должен состоять из 10 символов") String> phones;

    @Schema(description = "Стартовый баланс", example = "1000")
    private BigDecimal balance;
}


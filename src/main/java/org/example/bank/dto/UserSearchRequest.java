package org.example.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserSearchRequest {
    @Schema(description = "Рождён после", example = "1990-08-01")
    private LocalDate bornAfter;

    @Schema(description = "Телефонный номер", example = "9217237285")
    private String phone;

    @Schema(description = "ФИО", example = "Васильев Алексей Сергеевич")
    private String fullName;

    @Schema(description = "Адресо электронной почты", example = "email@fastmail.etc")
    private String email;
}

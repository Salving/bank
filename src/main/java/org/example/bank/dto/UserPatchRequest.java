package org.example.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserPatchRequest {
    @Schema(description = "Список адресов электронной почты", example = "[\"email@fastmail.etc\"]")
    @Size(min = 1)
    private List<@Email String> newEmails;

    @Schema(description = "Список телефонных номеров", example = "[\"9217237285\"]")
    @Size(min = 1)
    private List<@Size(min = 10, max = 10, message = "Телефон должен состоять из 10 символов") String> newPhones;

}

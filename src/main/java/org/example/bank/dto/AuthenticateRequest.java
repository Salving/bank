package org.example.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthenticateRequest {
    @NotBlank
    @Schema(example = "admin")
    private String username;
    @NotBlank
    @Schema(example = "password12345")
    private String password;
}

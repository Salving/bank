package org.example.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequest extends UserDto {
    @Schema(description = "Пароль", example = "password12345")
    @NotBlank
    @Size(min = 6, max = 30, message = "Пароль должен быть длиной от 6 до 30 символов")
    private String password;
}

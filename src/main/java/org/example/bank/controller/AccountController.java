package org.example.bank.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bank.dto.*;
import org.example.bank.entity.UserAccount;
import org.example.bank.service.TransactionService;
import org.example.bank.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AccountController {
    private final UserService userService;
    private final TransactionService transactionService;

    @Operation(description = "Создать аккаунт")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Пользователь был создан"), @ApiResponse(
            responseCode = "409",
            description = "Пользователь уже существует",
            content = {@Content}), @ApiResponse(responseCode = "400", content = {@Content})})
    @PostMapping
    public UserDto createAccount(@Valid @RequestBody UserCreateRequest user) {
        return userService.createAccount(user);
    }

    @PostMapping("/authenticate")
    public AuthenticateResponse authenticate(@RequestBody AuthenticateRequest authenticateRequest) {
        return userService.authenticate(authenticateRequest);
    }


    @GetMapping
    public Page<UserDto> search(@Parameter(description = "Параметры поиска (Могут быть пустыми)", explode = Explode.TRUE) UserSearchRequest searchRequest,
                                @Parameter(description = "Параметры пагинации (Могут быть пустыми)",
            explode = Explode.TRUE,
            example = """
                    {"page": 0, \n"size": 1, \n"sort": ["asc"]}
                    """) Pageable pageable) {
        return userService.findUsers(searchRequest, pageable);
    }

    @ApiResponses({@ApiResponse(responseCode = "200", description = "Пользователь был изменён"), @ApiResponse(
            responseCode = "409",
            description = "Электронная почта или телефон заняты",
            content = {@Content}), @ApiResponse(responseCode = "400", content = {@Content})})
    @PatchMapping("/self")
    public UserDto patchAccount(@Valid @RequestBody UserPatchRequest user) {
        return userService.changeUser(user);
    }

    @PostMapping("/self/transfer")
    public TransferResponse transfer(@Valid @RequestBody TransferRequest transferRequest, @AuthenticationPrincipal UserAccount user) {
        return transactionService.transfer(user, transferRequest);
    }
}

package org.example.bank.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bank.dto.*;
import org.example.bank.entity.Transaction;
import org.example.bank.entity.UserAccount;
import org.example.bank.exception.UserExistsException;
import org.example.bank.repository.TransactionRepository;
import org.example.bank.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto createAccount(UserCreateRequest userDto) {
        log.info("Creating account for {}", userDto.getUsername());

        if (userRepository.existsByUsername(userDto.getUsername()))
            throw new UserExistsException("Пользователь с таким логином уже существует");
        if (userRepository.existsByEmails(userDto.getEmails()))
            throw new UserExistsException("Пользователь с таким адресом электронной почты уже существует");
        if (userRepository.existsByPhones(userDto.getPhones()))
            throw new UserExistsException("Пользователь с таким номером телефона уже существует");

        UserAccount saved = userRepository.save(toNewUserAccount(userDto));

        transactionRepository.save(new Transaction(saved, userDto.getBalance()));

        log.info("Created account for {}", userDto.getUsername());
        return toUserDto(saved);
    }

    public Page<UserDto> findUsers(UserSearchRequest searchRequest, Pageable pageable) {
        log.info("Finding users for {}", searchRequest);
        return userRepository.search(searchRequest.getBornAfter(), searchRequest.getPhone(),
                        searchRequest.getFullName(), searchRequest.getEmail(), pageable)
                .map(this::toUserDto);
    }

    @Transactional
    public UserDto changeUser(UserPatchRequest user) {
        UserAccount account = getCurrentUser();
        log.info("Changing account for {}", account.getUsername());

        var newEmails = user.getNewEmails();
        if (newEmails != null) {
            log.info("Changing emails: {}", newEmails);
            List<String> accountEmails = account.getEmails();
            accountEmails.retainAll(newEmails);

            newEmails.removeAll(accountEmails);

            if (userRepository.existsByEmails(newEmails))
                throw new UserExistsException("Пользователь с таким адресом электронной почты уже существует");
            accountEmails.addAll(newEmails);
        }

        var newPhones = user.getNewPhones();
        if (newPhones != null) {
            log.info("Changing phones: {}", newPhones);
            List<String> accountPhones = account.getPhones();
            accountPhones.retainAll(newPhones);

            newPhones.removeAll(accountPhones);

            if (userRepository.existsByPhones(newPhones))
                throw new UserExistsException("Пользователь с таким номером телефона уже существует");
            accountPhones.addAll(newPhones);
        }

        UserAccount saved = userRepository.save(account);

        log.info("Changed account for {}", account.getUsername());
        return toUserDto(saved);
    }

    public UserAccount getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByUsername(username);
    }

    public AuthenticateResponse authenticate(AuthenticateRequest authenticateRequest) {
        log.info("Authenticating user {}", authenticateRequest.getUsername());

        UserDetails user = loadUserByUsername(authenticateRequest.getUsername());
        boolean authorized = passwordEncoder.matches(authenticateRequest.getPassword(), user.getPassword());
        if (!authorized) throw new BadCredentialsException("Неправильный логин или пароль");

        return new AuthenticateResponse(jwtService.generateToken(user));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    private UserDto toUserDto(UserAccount account) {
        BigDecimal balance = transactionRepository.findAmountByAccount(account);

        return UserDto.builder()
                .username(account.getUsername())
                .fullName(account.getFullName())
                .birthDate(account.getBirthDate())
                .balance(balance)
                .emails(account.getEmails())
                .phones(account.getPhones())
                .build();
    }

    private UserAccount toNewUserAccount(UserCreateRequest userDto) {
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        return UserAccount.builder()
                .username(userDto.getUsername())
                .password(encodedPassword)
                .fullName(userDto.getFullName())
                .birthDate(userDto.getBirthDate())
                .emails(userDto.getEmails())
                .phones(userDto.getPhones())
                .build();
    }
}

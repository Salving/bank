package org.example.bank.repository;

import org.example.bank.entity.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface UserRepository extends JpaRepository<UserAccount, Long> {
    UserAccount findByUsername(String username);

    @Query("""
            select u from UserAccount u where
            (cast(:bornAfter as date) is null or u.birthDate > :bornAfter) and
            (:phone is null or :phone member of u.phones) and
            (:fullName is null or u.fullName like :fullName%) and
            (:email is null or :email member of u.emails)
            """)
    Page<UserAccount> search(LocalDate bornAfter, String phone, String fullName, String email, Pageable pageable);

    boolean existsByUsername(String username);

    @Query("select case when count(*) > 0 then true else false end from UserAccount u join u.emails e where e = :email")
    boolean existsByEmail(String email);

    @Query("select case when count(*) > 0 then true else false end from UserAccount u join u.emails e where e in :emails")
    boolean existsByEmails(List<String> emails);

    @Query("select case when count(*) > 0 then true else false end from UserAccount u join u.phones p where p = :phone")
    boolean existsByPhone(String phone);

    @Query("select case when count(*) > 0 then true else false end from UserAccount u join u.phones p where p in :phones")
    boolean existsByPhones(List<String> phones);
}

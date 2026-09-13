package com.example.collaboration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CollabRepository extends JpaRepository<CollabEntity, Long> {
    boolean existsByPhone(String phone);

    Optional<Object> findByPhone(@NotBlank(message = "전화번호는 필수 입력 값입니다.") @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "올바른 전화번호 형식(010-0000-0000)이 아닙니다.") String phone);
}

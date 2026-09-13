package com.example.collaboration;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class CollabEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Size(min = 2, max = 10, message = "이름은 2자 이상 10자 이하로 입력해주세요.")
    private String name;

    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "올바른 전화번호 형식(010-0000-0000)이 아닙니다.")
    @Column(unique = true) // 👈 DB 레벨에서 전화번호 중복을 원천 차단!
    private String phone;

    @NotBlank(message = "메일주소는 필수 입력 값입니다.")
    @Email(message = "올바른 이메일 형식(abc@example.com)이 아닙니다.") // 👈 메일 전용 검증 어노테이션!
    private String mail;
}
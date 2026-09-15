# 🛒 콜라보레이션 응모 및 추첨 시스템 (Collab Project)

## 1. 프로젝트 개요 (Overview)
* **개발 기간**: 2026.09 ~ 2026.09 (개인 프로젝트, 1인)
* **프로젝트 소개**: 
  * 라멘가게 콜라보 이벤트나 애니메이션 굿즈 응모 시 회원가입 없이 이름과 전화번호, 주소만 입력하면 응모되는 시스템에서 착안함.
  * 응모 후 불확실함 때문에 뒤로가기를 눌러 동일한 정보로 재응모가 가능해지는 허점을 발견하였고, 이를 극복하여 **중복 응모를 차단하는 백엔드 로직**을 구현함.

## 2. 기술 스택 (Tech Stack)
* **Backend**: Java 17+, Spring Boot, Spring Data JPA
* **Frontend**: Thymeleaf, HTML5, CSS
* **Database**: H2 Database (In-Memory DB)
* **Tools**: Git, GitHub, IntelliJ IDEA, Gradle

## 3. 핵심 기능 (Key Features)
* **예외 처리 (Exception Handling)**: 
  * 개인이 여러 번 응모를 진행할 경우 転売屋(리셀러)로 인한 피해가 예상되므로 이를 차단할 방식을 고안함.
  * `IllegalArgumentException`을 발생시켜 한 번 입력된 전화번호로는 두 번 이상 응모할 수 없도록 차단하고, `@Column(unique = true)`를 통해 DB 레벨에서도 방어함.
* **유효성 검증 (Validation)**: 
  * 신원 정보가 확실해야 추첨에 지장이 없으므로 데이터 레벨에서 이름, 전화번호, 메일 형식을 지정함.
  * `@Valid`를 활용하여 유효하지 않은 입력값이 들어올 경우 경고 문구가 뜨도록 설정함.
* **동시성 제어 테스트**: 
  * 프로그램 구현 이전 서비스 테스트에서 `@DisplayName`을 활용해 같은 번호로 10명이 동시에 응모하더라도 1명만 성공하게끔 예외를 처리함.
* **관리자 페이지 및 추첨 시스템 (Admin & Draw)**:
  * 응모된 데이터를 관리자가 한눈에 조회하고 관리할 수 있는 관리자 전용 페이지 구현.
  * 등록된 응모자 명단 중에서 무작위로 당첨자를 선정하는 추첨 로직을 추가하여 실제 서비스의 완성도를 높임.

## 4. 아키텍처 및 데이터 흐름 (Architecture)
* **Layered Architecture (계층형 구조)** 적용
  * `Controller` ➔ `Service` ➔ `Repository` ➔ `Database` 순으로 관심사 분리(Separation of Concerns)를 고려하여 설계함.
  * 각각의 역할로는
  * Controller: 응모 페이지와 요청을 처리하고 결과를 반환
  * Service: 응모 등록과 중복 여부 판단
  * Repository: 응모 데이터를 조회·저장
  * Entity: 응모 데이터를 DB 테이블과 매핑

## 5. 트러블슈팅 및 배운 점 (Troubleshooting)
* 처음에는 중복 여부를 단순한 Boolean 값으로 처리하려 했습니다. 이후 같은 전화번호로 여러 요청이 들어오는 상황을 고려해 중복 판단 기준을 정하고, Service의 중복 검사와 DB UNIQUE 제약을 적용했습니다. 동일 번호로 재응모했을 때 중복 처리가 되는지 확인했습니다.이후 결과에서도 문제 없이 실행되는 것을 확인할 수 있었습니다.

package com.example.collaboration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CollabServiceTest {

    @Autowired
    private CollabService collabService;

    @Test
    @DisplayName("정상 응모 테스트")
    void registerSuccess() {
        // given
        CollabEntity entity = new CollabEntity();
        entity.setName("홍길동");
        entity.setPhone("010-1234-5678");
        entity.setMail("hong@example.com");

        // when & then
        collabService.register(entity);
    }

    @Test
    @DisplayName("중복 전화번호 응모 방어 테스트")
    void duplicatePhoneTest() {
        // given
        CollabEntity entity1 = new CollabEntity();
        entity1.setName("김철수");
        entity1.setPhone("010-9999-9999");
        entity1.setMail("철수@example.com");

        collabService.register(entity1);

        // when
        CollabEntity entity2 = new CollabEntity();
        entity2.setName("이영희");
        entity2.setPhone("010-9999-9999");
        entity2.setMail("영희@example.com");

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            collabService.register(entity2);
        });
    }

    @Test
    @DisplayName("동시성 제어 - 동시에 동일 번호로 10명이 응모해도 1명만 성공해야 함")
    void concurrentRegisterTest() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // given
        String sharedPhone = "010-7777-8888";

        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    CollabEntity entity = new CollabEntity();
                    entity.setName("참여자_" + index);
                    entity.setPhone(sharedPhone);
                    entity.setMail("test" + index + "@example.com");

                    collabService.register(entity);
                } catch (Exception e) {
                    // 중복 예외 방어 정상 동작
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        // then: DB에 해당 번호로 등록된 데이터는 오직 1개뿐이어야 함
        long count = collabService.getAllApplicants().stream()
                .filter(a -> a.getPhone().equals(sharedPhone))
                .count();

        assertThat(count).isEqualTo(1);
    }
}
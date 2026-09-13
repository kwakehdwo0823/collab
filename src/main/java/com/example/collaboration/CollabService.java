package com.example.collaboration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // 생성자 주입을 자동으로 해주는 마법의 어노테이션
public class CollabService {

    private final CollabRepository collabRepository;

    @Transactional
    public void register(CollabEntity collabEntity) {
        // 1차 방어: 자바 애플리케이션 단 중복 체크
        if (collabRepository.findByPhone(collabEntity.getPhone()).isPresent()) {
            throw new IllegalArgumentException("이미 응모된 전화번호입니다.");
        }

        try {
            // 저장 시도
            collabRepository.save(collabEntity);
        } catch (DataIntegrityViolationException e) {
            // 2차 방어: 동시에 몰려서 1차 체크를 뚫고 들어왔더라도 DB의 Unique 제약에 의해 여기서 완벽 차단!
            throw new IllegalArgumentException("이미 응모된 전화번호입니다. (동시 요청 차단)");
        }
    }

    // 1. 전체 응모자 목록 조회
    public List<CollabEntity> getAllApplicants() {
        return collabRepository.findAll();
    }

    // 2. 무작위 당첨자 추첨
    public CollabEntity drawWinner() {
        List<CollabEntity> applicants = collabRepository.findAll();
        if (applicants.isEmpty()) {
            throw new IllegalStateException("응모한 사람이 없습니다!");
        }

        Random random = new Random();
        int randomIndex = random.nextInt(applicants.size());
        return applicants.get(randomIndex);
    }
}


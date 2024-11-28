package org.userservice.userservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String REDIS_KEY_USER_SCORES = "user_scores"; // Redis에 저장될 키

    // Redis 에서 사용자 점수를 업데이트하는 메서드
    public void updateScore(String userId,int score, int solvedCount) {
        // 조합 점수 계산: 점수는 높을수록, 해결 문제 수는 적을수록 유리
        double compositeScore = score * 1_000_000.0 - solvedCount;
        redisTemplate.opsForZSet().add(REDIS_KEY_USER_SCORES, userId, compositeScore);
    }

    // 사용자 순위 반환 (1위부터 시작)
    public Long getUserRank(String userId) {
        Long rank = redisTemplate.opsForZSet().reverseRank(REDIS_KEY_USER_SCORES, userId);
        if (rank == null) {
            return 0L;
        } else {
            return rank + 1;
        }
    }
}

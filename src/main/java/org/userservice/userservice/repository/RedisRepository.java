package org.userservice.userservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Set;

//@Repository
//@RequiredArgsConstructor
//public class RedisRepository {
//
//    private final RedisTemplate<String, String> redisTemplate;
//    private static final String REDIS_KEY_USER_SCORES = "user_scores"; // Redis에 저장될 키
//
//    // Redis에서 사용자 점수를 업데이트하는 메서드
//    public void updateScore(String userId, int updatedScore) {
//        redisTemplate.opsForZSet().add(REDIS_KEY_USER_SCORES, userId, updatedScore);
//    }
//
//    //전체 사용자 순위 갱신
//    public List<UserRankDTO> getAllUserRanks() {
//        // Redis에서 내림차순으로 사용자 순위를 가져옴
//        Set<ZSetOperations.TypedTuple<String>> userScores = redisTemplate.opsForZSet()
//                .reverseRangeWithScores(REDIS_KEY_USER_SCORES, 0, -1); // 점수 내림차순 정렬
//
//        List<UserRankDTO> ranks = new ArrayList<>();
//        int rank = 1;
//        if (userScores != null) {
//            for (ZSetOperations.TypedTuple<String> entry : userScores) {
//                ranks.add(new UserRankDTO(rank++, entry.getValue(), entry.getScore()));
//            }
//        }
//        return ranks;
//    }
//
//    // Redis에서 해당 사용자의 순위를 가져오는 메서드
//    public Long getUserRank(String userId) {
//        // reverseRank는 높은 점수일수록 낮은 순위를 가질 수 있도록 함
//        return redisTemplate.opsForZSet().reverseRank(REDIS_KEY_USER_SCORES, userId);
//    }
//}

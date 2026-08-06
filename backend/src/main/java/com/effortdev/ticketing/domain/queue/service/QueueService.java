package com.effortdev.ticketing.domain.queue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Redis Sorted Set 기반 대기열 서비스.
 * key: queue:{eventId}, score: 진입 시각(timestamp), member: userId
 *
 * TODO:
 *  - enterQueue(eventId, userId): ZADD로 순번 등록
 *  - getRank(eventId, userId): ZRANK로 현재 순번 조회
 *  - 순번 도달 시 웹소켓으로 알림 발송 (스케줄러 or 별도 워커)
 */
@Service
@RequiredArgsConstructor
public class QueueService {

    private final RedisTemplate<String, String> redisTemplate;

    private String queueKey(Long eventId) {
        return "queue:" + eventId;
    }

    // TODO: 구현 예정
}

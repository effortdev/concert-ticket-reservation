package com.effortdev.ticketing.domain.queue.service;

import com.effortdev.ticketing.common.exception.CustomException;
import com.effortdev.ticketing.domain.event.entity.Event;
import com.effortdev.ticketing.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final RedisTemplate<String, String> redisTemplate;
    private final EventRepository eventRepository;

    /**
     * 대기열 진입. 이미 진입한 유저면 기존 순번을 그대로 반환한다 (중복 진입 방지).
     */
    public long enterQueue(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 공연입니다."));

        if (LocalDateTime.now().isBefore(event.getBookingOpenAt())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "아직 예매 오픈 전입니다.");
        }

        String queueKey = queueKey(eventId);
        String member = String.valueOf(userId);

        // 원자적 순번 발급 (INCR)
        Long sequence = redisTemplate.opsForValue().increment(sequenceKey(eventId));

        // ZADD NX: 이미 대기열에 있으면 score를 덮어쓰지 않고 false 반환
        Boolean added = redisTemplate.opsForZSet().addIfAbsent(queueKey, member, sequence);

        if (Boolean.FALSE.equals(added)) {
            // 이미 진입한 유저 -> 기존 순번 그대로 반환
            return getRank(eventId, userId);
        }

        return getRank(eventId, userId);
    }

    /**
     * 현재 순번 조회 (1부터 시작하는 순번으로 변환해서 반환)
     */
    public long getRank(Long eventId, Long userId) {
        String member = String.valueOf(userId);
        Long rank = redisTemplate.opsForZSet().rank(queueKey(eventId), member);

        if (rank == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "대기열에 진입하지 않았습니다.");
        }

        return rank + 1; // ZRANK는 0부터 시작하므로 +1
    }

    /**
     * 현재 대기 인원 수
     */
    public long getQueueSize(Long eventId) {
        Long size = redisTemplate.opsForZSet().zCard(queueKey(eventId));
        return size == null ? 0 : size;
    }

    private String queueKey(Long eventId) {
        return "queue:" + eventId;
    }

    private String sequenceKey(Long eventId) {
        return "queue:seq:" + eventId;
    }
}
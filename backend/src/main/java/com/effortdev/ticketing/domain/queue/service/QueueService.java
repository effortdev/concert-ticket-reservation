package com.effortdev.ticketing.domain.queue.service;

import com.effortdev.ticketing.common.exception.CustomException;
import com.effortdev.ticketing.domain.event.entity.Event;
import com.effortdev.ticketing.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class QueueService {

    private static final long ENTRY_BATCH_SIZE = 10;
    private static final long ENTRY_SESSION_MINUTES = 10; // 입장 완료 후 예매 유효 시간

    private final RedisTemplate<String, String> redisTemplate;
    private final EventRepository eventRepository;

    public long enterQueue(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 공연입니다."));

        if (LocalDateTime.now().isBefore(event.getBookingOpenAt())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "아직 예매 오픈 전입니다.");
        }

        String queueKey = queueKey(eventId);
        String member = String.valueOf(userId);

        Long sequence = redisTemplate.opsForValue().increment(sequenceKey(eventId));
        redisTemplate.opsForZSet().addIfAbsent(queueKey, member, sequence);

        return getRank(eventId, userId);
    }

    public long getRank(Long eventId, Long userId) {
        String member = String.valueOf(userId);
        Long rank = redisTemplate.opsForZSet().rank(queueKey(eventId), member);

        if (rank == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "대기열에 진입하지 않았습니다.");
        }
        return rank + 1;
    }

    public long getQueueSize(Long eventId) {
        Long size = redisTemplate.opsForZSet().zCard(queueKey(eventId));
        return size == null ? 0 : size;
    }

    public long getAllowedRank(Long eventId) {
        String value = redisTemplate.opsForValue().get(allowedKey(eventId));
        return value == null ? 0 : Long.parseLong(value);
    }

    public long advanceQueue(Long eventId) {
        long queueSize = getQueueSize(eventId);
        if (queueSize == 0) {
            return getAllowedRank(eventId);
        }

        long current = getAllowedRank(eventId);
        long next = Math.min(current + ENTRY_BATCH_SIZE, queueSize);

        if (next != current) {
            redisTemplate.opsForValue().set(allowedKey(eventId), String.valueOf(next));
        }
        return next;
    }

    /**
     * 대기열 순번이 허용 범위 안이면 "입장 완료"로 기록한다.
     * 입장 기록은 TTL을 둬서, 일정 시간 안에 예매를 완료하지 않으면 자동 만료된다.
     */
    public void confirmEntry(Long eventId, Long userId) {
        long myRank = getRank(eventId, userId);
        long allowedRank = getAllowedRank(eventId);

        if (myRank > allowedRank) {
            throw new CustomException(HttpStatus.FORBIDDEN, "아직 입장 순서가 아닙니다.");
        }

        redisTemplate.opsForSet().add(enteredKey(eventId), String.valueOf(userId));
        redisTemplate.expire(enteredKey(eventId), ENTRY_SESSION_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 이 유저가 입장 완료 상태인지 확인한다. 좌석 홀딩 API에서 사용.
     */
    public boolean isEntered(Long eventId, Long userId) {
        Boolean isMember = redisTemplate.opsForSet().isMember(enteredKey(eventId), String.valueOf(userId));
        return Boolean.TRUE.equals(isMember);
    }

    private String queueKey(Long eventId) {
        return "queue:" + eventId;
    }

    private String sequenceKey(Long eventId) {
        return "queue:seq:" + eventId;
    }

    private String allowedKey(Long eventId) {
        return "queue:allowed:" + eventId;
    }

    private String enteredKey(Long eventId) {
        return "queue:entered:" + eventId;
    }
}
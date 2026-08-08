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

    private static final long ENTRY_BATCH_SIZE = 10; // 한 번에 입장 허용할 인원

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
        Boolean added = redisTemplate.opsForZSet().addIfAbsent(queueKey, member, sequence);

        if (Boolean.FALSE.equals(added)) {
            return getRank(eventId, userId);
        }
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

    /**
     * 현재 입장 허용된 순번(커서)을 조회한다. 아직 한 번도 진행 안 됐으면 0.
     */
    public long getAllowedRank(Long eventId) {
        String value = redisTemplate.opsForValue().get(allowedKey(eventId));
        return value == null ? 0 : Long.parseLong(value);
    }

    /**
     * 허용 순번을 배치 크기만큼 전진시킨다. 대기열 크기를 넘지 않도록 캡을 씌운다.
     * @return 전진 후의 허용 순번. 변화가 없었다면 이전 값과 동일.
     */
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

    private String queueKey(Long eventId) {
        return "queue:" + eventId;
    }

    private String sequenceKey(Long eventId) {
        return "queue:seq:" + eventId;
    }

    private String allowedKey(Long eventId) {
        return "queue:allowed:" + eventId;
    }
}
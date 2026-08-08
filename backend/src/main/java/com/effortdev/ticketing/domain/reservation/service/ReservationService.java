package com.effortdev.ticketing.domain.reservation.service;

import com.effortdev.ticketing.common.exception.CustomException;
import com.effortdev.ticketing.domain.queue.service.QueueService;
import com.effortdev.ticketing.domain.reservation.dto.ReservationHoldRequest;
import com.effortdev.ticketing.domain.reservation.dto.ReservationHoldResponse;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final long LOCK_WAIT_SECONDS = 3;

    private final RedissonClient redissonClient;
    private final QueueService queueService;
    private final ReservationTransactionalService reservationTransactionalService;

    public ReservationHoldResponse holdSeat(ReservationHoldRequest request, Long userId) {

        if (!queueService.isEntered(request.getEventId(), userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "대기열 입장을 먼저 완료해주세요.");
        }

        RLock lock = redissonClient.getLock("lock:seat:" + request.getSeatId());

        boolean locked;
        try {
            locked = lock.tryLock(LOCK_WAIT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "좌석 선점 처리 중 오류가 발생했습니다.");
        }

        if (!locked) {
            throw new CustomException(HttpStatus.CONFLICT, "다른 사용자가 이 좌석을 처리 중입니다. 잠시 후 다시 시도해주세요.");
        }

        try {
            // 이 호출이 리턴됐다는 건, 프록시를 통과한 별도 트랜잭션이 이미 커밋 완료됐다는 뜻.
            // 그래야 락을 풀어도 다음 스레드가 "최신 커밋된 상태"를 보게 됨.
            return reservationTransactionalService.holdSeatInTransaction(request, userId);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
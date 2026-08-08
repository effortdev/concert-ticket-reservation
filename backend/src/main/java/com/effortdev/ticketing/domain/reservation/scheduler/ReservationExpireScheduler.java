package com.effortdev.ticketing.domain.reservation.scheduler;

import com.effortdev.ticketing.domain.reservation.entity.Reservation;
import com.effortdev.ticketing.domain.reservation.repository.ReservationRepository;
import com.effortdev.ticketing.domain.reservation.service.ReservationExpireTransactionalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationExpireScheduler {

    private static final long LOCK_WAIT_SECONDS = 1;

    private final ReservationRepository reservationRepository;
    private final RedissonClient redissonClient;
    private final ReservationExpireTransactionalService reservationExpireTransactionalService;

    @Scheduled(fixedRate = 30000)
    public void expireHoldings() {
        List<Reservation> expired = reservationRepository.findByStatusAndHoldExpiresAtBefore(
                Reservation.Status.HOLDING, LocalDateTime.now()
        );

        for (Reservation reservation : expired) {
            expireOne(reservation.getId(), reservation.getSeatId());
        }
    }

    private void expireOne(Long reservationId, Long seatId) {
        RLock lock = redissonClient.getLock("lock:seat:" + seatId);

        boolean locked;
        try {
            locked = lock.tryLock(LOCK_WAIT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        if (!locked) {
            log.warn("좌석 만료 처리 락 획득 실패 - seatId: {}", seatId);
            return;
        }

        try {
            reservationExpireTransactionalService.expireInTransaction(reservationId, seatId);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
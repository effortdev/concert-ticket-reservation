package com.effortdev.ticketing.domain.reservation.service;

import com.effortdev.ticketing.common.exception.CustomException;
import com.effortdev.ticketing.domain.queue.service.QueueService;
import com.effortdev.ticketing.domain.reservation.dto.ReservationConfirmResponse;
import com.effortdev.ticketing.domain.reservation.dto.ReservationHoldRequest;
import com.effortdev.ticketing.domain.reservation.dto.ReservationHoldResponse;
import com.effortdev.ticketing.domain.reservation.entity.Reservation;
import com.effortdev.ticketing.domain.reservation.repository.ReservationRepository;
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
    private final ReservationRepository reservationRepository;
    private final ReservationTransactionalService reservationTransactionalService;
    private final ReservationConfirmTransactionalService reservationConfirmTransactionalService;
    private final MockPaymentService mockPaymentService;

    public ReservationHoldResponse holdSeat(ReservationHoldRequest request, Long userId) {
        if (!queueService.isEntered(request.getEventId(), userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "대기열 입장을 먼저 완료해주세요.");
        }

        RLock lock = redissonClient.getLock("lock:seat:" + request.getSeatId());
        boolean locked = tryLock(lock);

        try {
            return reservationTransactionalService.holdSeatInTransaction(request, userId);
        } finally {
            unlock(lock);
        }
    }

    public ReservationConfirmResponse confirmReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다."));

        RLock lock = redissonClient.getLock("lock:seat:" + reservation.getSeatId());
        boolean locked = tryLock(lock);

        try {
            boolean approved = mockPaymentService.approve(reservationId, null);

            if (!approved) {
                reservationConfirmTransactionalService.cancelHold(reservationId);
                throw new CustomException(HttpStatus.PAYMENT_REQUIRED, "결제가 거절되었습니다. 다시 시도해주세요.");
            }

            return reservationConfirmTransactionalService.confirmInTransaction(reservationId, userId);
        } finally {
            unlock(lock);
        }
    }

    private boolean tryLock(RLock lock) {
        try {
            boolean locked = lock.tryLock(LOCK_WAIT_SECONDS, TimeUnit.SECONDS);
            if (!locked) {
                throw new CustomException(HttpStatus.CONFLICT, "다른 요청이 처리 중입니다. 잠시 후 다시 시도해주세요.");
            }
            return locked;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "처리 중 오류가 발생했습니다.");
        }
    }

    private void unlock(RLock lock) {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
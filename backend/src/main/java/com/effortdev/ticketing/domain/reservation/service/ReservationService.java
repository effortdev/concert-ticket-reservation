package com.effortdev.ticketing.domain.reservation.service;

import com.effortdev.ticketing.common.exception.CustomException;
import com.effortdev.ticketing.domain.queue.service.QueueService;
import com.effortdev.ticketing.domain.reservation.dto.ReservationHoldRequest;
import com.effortdev.ticketing.domain.reservation.dto.ReservationHoldResponse;
import com.effortdev.ticketing.domain.reservation.entity.Reservation;
import com.effortdev.ticketing.domain.reservation.repository.ReservationRepository;
import com.effortdev.ticketing.domain.seat.entity.Seat;
import com.effortdev.ticketing.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final long HOLD_MINUTES = 5;
    private static final long LOCK_WAIT_SECONDS = 3;

    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final RedissonClient redissonClient;
    private final QueueService queueService;

    @Transactional
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
            Seat seat = seatRepository.findById(request.getSeatId())
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 좌석입니다."));

            if (!seat.getEventId().equals(request.getEventId())) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "해당 공연의 좌석이 아닙니다.");
            }

            try {
                seat.hold();
            } catch (IllegalStateException e) {
                throw new CustomException(HttpStatus.CONFLICT, "이미 선택된 좌석입니다.");
            }

            LocalDateTime holdExpiresAt = LocalDateTime.now().plusMinutes(HOLD_MINUTES);
            Reservation reservation = Reservation.builder()
                    .userId(userId)
                    .seatId(seat.getId())
                    .eventId(request.getEventId())
                    .holdExpiresAt(holdExpiresAt)
                    .build();

            Reservation saved = reservationRepository.save(reservation);
            return new ReservationHoldResponse(saved);

        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
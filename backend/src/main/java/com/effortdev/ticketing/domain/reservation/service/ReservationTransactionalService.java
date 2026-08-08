package com.effortdev.ticketing.domain.reservation.service;

import com.effortdev.ticketing.common.exception.CustomException;
import com.effortdev.ticketing.domain.reservation.dto.ReservationHoldRequest;
import com.effortdev.ticketing.domain.reservation.dto.ReservationHoldResponse;
import com.effortdev.ticketing.domain.reservation.entity.Reservation;
import com.effortdev.ticketing.domain.reservation.repository.ReservationRepository;
import com.effortdev.ticketing.domain.seat.entity.Seat;
import com.effortdev.ticketing.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationTransactionalService {

    private static final long HOLD_MINUTES = 5;

    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 실제 DB 작업(좌석 상태 변경 + 예약 생성)을 하나의 트랜잭션으로 처리한다.
     * 이 메서드가 리턴된 시점 = 트랜잭션이 완전히 커밋된 시점 (Spring AOP 프록시 경계).
     */
    @Transactional
    public ReservationHoldResponse holdSeatInTransaction(ReservationHoldRequest request, Long userId) {
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
    }
}
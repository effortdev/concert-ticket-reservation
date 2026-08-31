package com.effortdev.ticketing.domain.reservation.service;

import com.effortdev.ticketing.common.exception.CustomException;
import com.effortdev.ticketing.domain.reservation.dto.ReservationConfirmResponse;
import com.effortdev.ticketing.domain.reservation.entity.Reservation;
import com.effortdev.ticketing.domain.reservation.repository.ReservationRepository;
import com.effortdev.ticketing.domain.seat.entity.Seat;
import com.effortdev.ticketing.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationConfirmTransactionalService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public ReservationConfirmResponse confirmInTransaction(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다."));

        if (!reservation.getUserId().equals(userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "본인의 예약만 확정할 수 있습니다.");
        }

        try {
            reservation.confirm();
        } catch (IllegalStateException e) {
            throw new CustomException(HttpStatus.CONFLICT, "이미 처리되었거나 만료된 예약입니다.");
        }

        Seat seat = seatRepository.findById(reservation.getSeatId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 좌석입니다."));
        seat.sell();

        return new ReservationConfirmResponse(reservation);
    }

    @Transactional
    public void cancelHold(Long reservationId) {
        reservationRepository.findById(reservationId).ifPresent(reservation -> {
            if (reservation.getStatus() == Reservation.Status.HOLDING) {
                reservation.expire();
                seatRepository.findById(reservation.getSeatId())
                        .ifPresent(Seat::release);
            }
        });
    }
}
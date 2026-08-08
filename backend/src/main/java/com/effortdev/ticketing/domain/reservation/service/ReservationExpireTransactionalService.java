package com.effortdev.ticketing.domain.reservation.service;

import com.effortdev.ticketing.domain.reservation.entity.Reservation;
import com.effortdev.ticketing.domain.reservation.repository.ReservationRepository;
import com.effortdev.ticketing.domain.seat.entity.Seat;
import com.effortdev.ticketing.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationExpireTransactionalService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public void expireInTransaction(Long reservationId, Long seatId) {
        reservationRepository.findById(reservationId).ifPresent(reservation -> {
            if (reservation.getStatus() == Reservation.Status.HOLDING) {
                reservation.expire();
            }
        });

        seatRepository.findById(seatId).ifPresent(seat -> {
            if (seat.getStatus() == Seat.Status.HOLDING) {
                seat.release();
            }
        });
    }
}
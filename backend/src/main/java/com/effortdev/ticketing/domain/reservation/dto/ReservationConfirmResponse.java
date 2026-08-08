package com.effortdev.ticketing.domain.reservation.dto;

import com.effortdev.ticketing.domain.reservation.entity.Reservation;
import lombok.Getter;

@Getter
public class ReservationConfirmResponse {

    private final Long reservationId;
    private final Long seatId;
    private final String status;

    public ReservationConfirmResponse(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.seatId = reservation.getSeatId();
        this.status = reservation.getStatus().name();
    }
}
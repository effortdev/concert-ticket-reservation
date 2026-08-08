package com.effortdev.ticketing.domain.reservation.dto;

import com.effortdev.ticketing.domain.reservation.entity.Reservation;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationHoldResponse {

    private final Long reservationId;
    private final Long seatId;
    private final String status;
    private final LocalDateTime holdExpiresAt;

    public ReservationHoldResponse(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.seatId = reservation.getSeatId();
        this.status = reservation.getStatus().name();
        this.holdExpiresAt = reservation.getHoldExpiresAt();
    }
}
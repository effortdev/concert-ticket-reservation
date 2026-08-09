package com.effortdev.ticketing.domain.reservation.dto;

import com.effortdev.ticketing.domain.reservation.entity.Reservation;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationSummaryResponse {

    private final Long reservationId;
    private final Long eventId;
    private final Long seatId;
    private final String status;
    private final LocalDateTime createdAt;

    public ReservationSummaryResponse(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.eventId = reservation.getEventId();
        this.seatId = reservation.getSeatId();
        this.status = reservation.getStatus().name();
        this.createdAt = reservation.getCreatedAt();
    }
}
package com.effortdev.ticketing.domain.seat.dto;

import com.effortdev.ticketing.domain.seat.entity.Seat;
import lombok.Getter;

@Getter
public class SeatResponse {

    private final Long id;
    private final String seatNumber;
    private final String grade;
    private final Integer price;
    private final String status;

    public SeatResponse(Seat seat) {
        this.id = seat.getId();
        this.seatNumber = seat.getSeatNumber();
        this.grade = seat.getGrade().name();
        this.price = seat.getPrice();
        this.status = seat.getStatus().name();
    }
}
package com.effortdev.ticketing.domain.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationHoldRequest {

    @NotNull(message = "공연 ID는 필수입니다.")
    private Long eventId;

    @NotNull(message = "좌석 ID는 필수입니다.")
    private Long seatId;
}
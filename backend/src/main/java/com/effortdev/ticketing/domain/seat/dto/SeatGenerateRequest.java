package com.effortdev.ticketing.domain.seat.dto;

import com.effortdev.ticketing.domain.seat.entity.Seat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SeatGenerateRequest {

    @NotNull(message = "좌석 등급은 필수입니다.")
    private Seat.Grade grade;

    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price;

    @NotNull(message = "생성할 좌석 수는 필수입니다.")
    @Min(value = 1, message = "1개 이상 생성해야 합니다.")
    private Integer count;
}
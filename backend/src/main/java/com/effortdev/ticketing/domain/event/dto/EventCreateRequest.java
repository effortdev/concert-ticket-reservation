package com.effortdev.ticketing.domain.event.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class EventCreateRequest {

    @NotBlank(message = "공연 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "공연 장소는 필수입니다.")
    private String venue;

    @NotNull(message = "공연 일시는 필수입니다.")
    @Future(message = "공연 일시는 미래 시각이어야 합니다.")
    private LocalDateTime eventDate;

    @NotNull(message = "예매 오픈 시각은 필수입니다.")
    @Future(message = "예매 오픈 시각은 미래 시각이어야 합니다.")
    private LocalDateTime bookingOpenAt;
}
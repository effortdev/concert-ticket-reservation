package com.effortdev.ticketing.controller;

import com.effortdev.ticketing.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationRepository reservationRepository;

    // TODO: POST /api/reservations/hold - 좌석 임시 선점 (Redis TTL + Redisson 분산 락)
    // TODO: POST /api/reservations/{id}/confirm - Mock 결제 승인 후 예매 확정
    // TODO: DELETE /api/reservations/{id} - 예매 취소
}

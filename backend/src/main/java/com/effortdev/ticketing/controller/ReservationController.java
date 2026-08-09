package com.effortdev.ticketing.controller;

import com.effortdev.ticketing.common.response.ApiResponse;
import com.effortdev.ticketing.domain.reservation.dto.ReservationConfirmResponse;
import com.effortdev.ticketing.domain.reservation.dto.ReservationHoldRequest;
import com.effortdev.ticketing.domain.reservation.dto.ReservationHoldResponse;
import com.effortdev.ticketing.domain.reservation.dto.ReservationSummaryResponse;
import com.effortdev.ticketing.domain.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/hold")
    public ApiResponse<ReservationHoldResponse> holdSeat(
            @Valid @RequestBody ReservationHoldRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResponse.success(reservationService.holdSeat(request, userId));
    }

    @PostMapping("/{reservationId}/confirm")
    public ApiResponse<ReservationConfirmResponse> confirmReservation(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResponse.success(reservationService.confirmReservation(reservationId, userId));
    }

    @GetMapping("/my")
    public ApiResponse<List<ReservationSummaryResponse>> getMyReservations(@AuthenticationPrincipal Long userId) {
        return ApiResponse.success(reservationService.getMyReservations(userId));
    }
}
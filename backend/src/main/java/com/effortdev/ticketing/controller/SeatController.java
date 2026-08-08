package com.effortdev.ticketing.controller;

import com.effortdev.ticketing.common.response.ApiResponse;
import com.effortdev.ticketing.domain.seat.dto.SeatGenerateRequest;
import com.effortdev.ticketing.domain.seat.dto.SeatResponse;
import com.effortdev.ticketing.domain.seat.service.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/events/{eventId}/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<SeatResponse>> generateSeats(
            @PathVariable Long eventId,
            @Valid @RequestBody SeatGenerateRequest request
    ) {
        return ApiResponse.success(seatService.generateSeats(eventId, request));
    }

    @GetMapping
    public ApiResponse<List<SeatResponse>> getSeats(@PathVariable Long eventId) {
        return ApiResponse.success(seatService.getSeats(eventId));
    }
}
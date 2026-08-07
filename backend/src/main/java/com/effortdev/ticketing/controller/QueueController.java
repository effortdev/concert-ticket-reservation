package com.effortdev.ticketing.controller;

import com.effortdev.ticketing.common.response.ApiResponse;
import com.effortdev.ticketing.domain.queue.dto.QueueStatusResponse;
import com.effortdev.ticketing.domain.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @PostMapping("/{eventId}/enter")
    public ApiResponse<QueueStatusResponse> enterQueue(
            @PathVariable Long eventId,
            @AuthenticationPrincipal Long userId
    ) {
        long rank = queueService.enterQueue(eventId, userId);
        return ApiResponse.success(new QueueStatusResponse(eventId, rank));
    }

    @GetMapping("/{eventId}/status")
    public ApiResponse<QueueStatusResponse> getStatus(
            @PathVariable Long eventId,
            @AuthenticationPrincipal Long userId
    ) {
        long rank = queueService.getRank(eventId, userId);
        return ApiResponse.success(new QueueStatusResponse(eventId, rank));
    }
}
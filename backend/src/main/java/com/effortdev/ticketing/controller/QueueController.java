package com.effortdev.ticketing.controller;

import com.effortdev.ticketing.domain.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    // TODO: POST /api/queue/{eventId}/enter - 대기열 진입, 순번 부여
    // TODO: GET /api/queue/{eventId}/status - 내 순번 조회 (또는 웹소켓 구독으로 대체)
}

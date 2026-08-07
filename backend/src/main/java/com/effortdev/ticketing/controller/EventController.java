package com.effortdev.ticketing.controller;

import com.effortdev.ticketing.common.response.ApiResponse;
import com.effortdev.ticketing.domain.event.dto.EventCreateRequest;
import com.effortdev.ticketing.domain.event.dto.EventResponse;
import com.effortdev.ticketing.domain.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EventResponse> createEvent(@Valid @RequestBody EventCreateRequest request) {
        return ApiResponse.success(eventService.createEvent(request));
    }

    @GetMapping("/{eventId}")
    public ApiResponse<EventResponse> getEvent(@PathVariable Long eventId) {
        return ApiResponse.success(eventService.getEvent(eventId));
    }

    @GetMapping
    public ApiResponse<List<EventResponse>> getEvents() {
        return ApiResponse.success(eventService.getEvents());
    }
}
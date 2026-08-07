package com.effortdev.ticketing.domain.event.dto;

import com.effortdev.ticketing.domain.event.entity.Event;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EventResponse {

    private final Long id;
    private final String title;
    private final String venue;
    private final LocalDateTime eventDate;
    private final LocalDateTime bookingOpenAt;
    private final String status;

    public EventResponse(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.venue = event.getVenue();
        this.eventDate = event.getEventDate();
        this.bookingOpenAt = event.getBookingOpenAt();
        this.status = event.getStatus().name();
    }
}
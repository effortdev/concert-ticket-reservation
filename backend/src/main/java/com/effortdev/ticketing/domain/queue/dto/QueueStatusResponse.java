package com.effortdev.ticketing.domain.queue.dto;

import lombok.Getter;

@Getter
public class QueueStatusResponse {

    private final Long eventId;
    private final long rank;

    public QueueStatusResponse(Long eventId, long rank) {
        this.eventId = eventId;
        this.rank = rank;
    }
}
package com.effortdev.ticketing.domain.queue.dto;

import lombok.Getter;

@Getter
public class QueueAdvanceMessage {

    private final Long eventId;
    private final long allowedRank;
    private final long queueSize;

    public QueueAdvanceMessage(Long eventId, long allowedRank, long queueSize) {
        this.eventId = eventId;
        this.allowedRank = allowedRank;
        this.queueSize = queueSize;
    }
}
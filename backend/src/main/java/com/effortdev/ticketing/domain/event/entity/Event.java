package com.effortdev.ticketing.domain.event.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String venue;
    private LocalDateTime eventDate;
    private LocalDateTime bookingOpenAt; // 예매 오픈 시각 (대기열이 열리는 기준 시각)

    @Enumerated(EnumType.STRING)
    private Status status;

    @Builder
    public Event(String title, String venue, LocalDateTime eventDate, LocalDateTime bookingOpenAt) {
        this.title = title;
        this.venue = venue;
        this.eventDate = eventDate;
        this.bookingOpenAt = bookingOpenAt;
        this.status = Status.SCHEDULED;
    }

    public enum Status {
        SCHEDULED, BOOKING_OPEN, CLOSED
    }
}

package com.effortdev.ticketing.domain.reservation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long seatId;
    private Long eventId;

    @Enumerated(EnumType.STRING)
    private Status status; // HOLDING, CONFIRMED, CANCELED, EXPIRED

    private LocalDateTime holdExpiresAt; // 좌석 홀딩 만료 시각 (Redis TTL과 별개로 DB에도 기록)
    private LocalDateTime createdAt;

    @Builder
    public Reservation(Long userId, Long seatId, Long eventId, LocalDateTime holdExpiresAt) {
        this.userId = userId;
        this.seatId = seatId;
        this.eventId = eventId;
        this.status = Status.HOLDING;
        this.holdExpiresAt = holdExpiresAt;
        this.createdAt = LocalDateTime.now();
    }

    public void confirm() {
        this.status = Status.CONFIRMED;
    }

    public void cancel() {
        this.status = Status.CANCELED;
    }

    public enum Status {
        HOLDING, CONFIRMED, CANCELED, EXPIRED
    }
}

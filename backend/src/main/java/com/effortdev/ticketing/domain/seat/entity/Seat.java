package com.effortdev.ticketing.domain.seat.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long eventId;
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    private Grade grade; // VIP, R, S

    private Integer price;

    @Enumerated(EnumType.STRING)
    private Status status; // AVAILABLE, HOLDING, SOLD

    // 낙관적 락 실험용 (비관적 락/분산 락과 비교할 때 사용)
    @Version
    private Long version;

    @Builder
    public Seat(Long eventId, String seatNumber, Grade grade, Integer price) {
        this.eventId = eventId;
        this.seatNumber = seatNumber;
        this.grade = grade;
        this.price = price;
        this.status = Status.AVAILABLE;
    }

    public enum Grade {
        VIP, R, S
    }

    public enum Status {
        AVAILABLE, HOLDING, SOLD
    }

    public void hold() {
        if (this.status != Status.AVAILABLE) {
            throw new IllegalStateException("이미 선점되었거나 판매된 좌석입니다.");
        }
        this.status = Status.HOLDING;
    }

    public void release() {
        this.status = Status.AVAILABLE;
    }

    public void sell() {
        this.status = Status.SOLD;
    }
}

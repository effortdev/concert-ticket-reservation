package com.effortdev.ticketing.domain.reservation.repository;

import com.effortdev.ticketing.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByStatusAndHoldExpiresAtBefore(Reservation.Status status, LocalDateTime now);
}
package com.effortdev.ticketing.domain.event.repository;

import com.effortdev.ticketing.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}

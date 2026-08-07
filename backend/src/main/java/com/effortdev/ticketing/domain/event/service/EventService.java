package com.effortdev.ticketing.domain.event.service;

import com.effortdev.ticketing.common.exception.CustomException;
import com.effortdev.ticketing.domain.event.dto.EventCreateRequest;
import com.effortdev.ticketing.domain.event.dto.EventResponse;
import com.effortdev.ticketing.domain.event.entity.Event;
import com.effortdev.ticketing.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    @Transactional
    public EventResponse createEvent(EventCreateRequest request) {
        if (request.getBookingOpenAt().isAfter(request.getEventDate())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "예매 오픈 시각은 공연 일시보다 빠를 수 없습니다.");
        }

        Event event = Event.builder()
                .title(request.getTitle())
                .venue(request.getVenue())
                .eventDate(request.getEventDate())
                .bookingOpenAt(request.getBookingOpenAt())
                .build();

        Event saved = eventRepository.save(event);
        return new EventResponse(saved);
    }

    public EventResponse getEvent(Long eventId) {
        Event event = findEventOrThrow(eventId);
        return new EventResponse(event);
    }

    public List<EventResponse> getEvents() {
        return eventRepository.findAll().stream()
                .map(EventResponse::new)
                .toList();
    }

    private Event findEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 공연입니다."));
    }
}
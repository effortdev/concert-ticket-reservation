package com.effortdev.ticketing.domain.seat.service;

import com.effortdev.ticketing.common.exception.CustomException;
import com.effortdev.ticketing.domain.event.repository.EventRepository;
import com.effortdev.ticketing.domain.seat.dto.SeatGenerateRequest;
import com.effortdev.ticketing.domain.seat.dto.SeatResponse;
import com.effortdev.ticketing.domain.seat.entity.Seat;
import com.effortdev.ticketing.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;

    @Transactional
    public List<SeatResponse> generateSeats(Long eventId, SeatGenerateRequest request) {
        if (!eventRepository.existsById(eventId)) {
            throw new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 공연입니다.");
        }

        List<Seat> seats = java.util.stream.IntStream.rangeClosed(1, request.getCount())
                .mapToObj(i -> Seat.builder()
                        .eventId(eventId)
                        .seatNumber(request.getGrade().name() + "-" + i)
                        .grade(request.getGrade())
                        .price(request.getPrice())
                        .build())
                .toList();

        return seatRepository.saveAll(seats).stream()
                .map(SeatResponse::new)
                .toList();
    }

    public List<SeatResponse> getSeats(Long eventId) {
        return seatRepository.findByEventId(eventId).stream()
                .map(SeatResponse::new)
                .toList();
    }
}
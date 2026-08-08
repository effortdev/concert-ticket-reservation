package com.effortdev.ticketing.domain.queue.scheduler;

import com.effortdev.ticketing.domain.event.entity.Event;
import com.effortdev.ticketing.domain.event.repository.EventRepository;
import com.effortdev.ticketing.domain.queue.dto.QueueAdvanceMessage;
import com.effortdev.ticketing.domain.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class QueueAdvanceScheduler {

    private final EventRepository eventRepository;
    private final QueueService queueService;
    private final SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedRate = 5000)
    public void advanceAllActiveQueues() {
        List<Event> openEvents = eventRepository.findAll().stream()
                .filter(e -> !LocalDateTime.now().isBefore(e.getBookingOpenAt()))
                .toList();

        for (Event event : openEvents) {
            Long eventId = event.getId();

            if (queueService.getQueueSize(eventId) == 0) {
                continue;
            }

            long allowedRank = queueService.advanceQueue(eventId);
            long queueSize = queueService.getQueueSize(eventId);

            QueueAdvanceMessage message = new QueueAdvanceMessage(eventId, allowedRank, queueSize);
            messagingTemplate.convertAndSend("/sub/queue/" + eventId, message);
        }
    }
}
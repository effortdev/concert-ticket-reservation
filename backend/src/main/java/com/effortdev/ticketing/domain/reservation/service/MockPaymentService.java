package com.effortdev.ticketing.domain.reservation.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 실제 PG(포트원 등) 연동 없이 결제 승인을 흉내내는 Mock 서비스.
 * 나중에 실제 PG로 교체할 때 이 클래스만 바꾸면 되도록 인터페이스 형태로 분리해둠.
 */
@Service
public class MockPaymentService {

    private static final double SUCCESS_RATE = 0.9; // 90% 성공, 10% 실패 시뮬레이션

    public boolean approve(Long reservationId, Integer amount) {
        return ThreadLocalRandom.current().nextDouble() < SUCCESS_RATE;
    }
}
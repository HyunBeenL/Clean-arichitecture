package com.example.srp.ch07;

import java.time.LocalDate;
import java.util.List;

/**
 * 테스트용 근무 기록 픽스처.
 *
 * <p>월 10h, 화 10h, 수 4h, 목 4h, 금 4h — <b>주 합계 32시간</b>
 *
 * <p>이 기록이 중요한 이유: 주 합계는 40시간을 넘지 않지만,
 * 월·화는 하루 8시간을 넘겼다. 그래서 "하루 8시간 기준"과 "주 40시간 기준"의
 * 계산 결과가 서로 달라진다.
 */
final class TimeCards {

    private TimeCards() {
    }

    static List<TimeCard> oneWeek() {
        return List.of(
                new TimeCard(LocalDate.of(2026, 3, 2), 10),  // 월
                new TimeCard(LocalDate.of(2026, 3, 3), 10),  // 화
                new TimeCard(LocalDate.of(2026, 3, 4), 4),   // 수
                new TimeCard(LocalDate.of(2026, 3, 5), 4),   // 목
                new TimeCard(LocalDate.of(2026, 3, 6), 4)    // 금
        );
    }
}

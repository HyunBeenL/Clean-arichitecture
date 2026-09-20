package com.example.srp.ch07;

import java.time.LocalDate;
import java.util.List;

/**
 * 테스트용 근무 기록 픽스처. problem / solution 양쪽 테스트가 함께 쓴다.
 *
 * <p>근무 기록은 "김개발이 이렇게 일했다"는 <b>사실</b>일 뿐,
 * 어느 부서의 규칙도 담고 있지 않다. 그래서 공유해도 안전하다.
 */
public final class TimeCards {

    private static final LocalDate MONDAY = LocalDate.of(2026, 3, 2);

    private TimeCards() {
    }

    /**
     * 월 10h, 화 10h, 수 4h, 목 4h, 금 4h — <b>주 합계 32시간</b>
     *
     * <p>주 40시간은 넘지 않지만 월·화는 하루 8시간을 넘겼다.
     * 그래서 회계팀 기준(주 40시간)과 인사팀 기준(하루 8시간)의 결과가 갈린다.
     */
    public static List<TimeCard> oneWeek() {
        return List.of(
                new TimeCard(MONDAY, 10),
                new TimeCard(MONDAY.plusDays(1), 10),
                new TimeCard(MONDAY.plusDays(2), 4),
                new TimeCard(MONDAY.plusDays(3), 4),
                new TimeCard(MONDAY.plusDays(4), 4)
        );
    }

    /** 하루 9시간씩 5일 — 주 합계 45시간. */
    public static List<TimeCard> fortyFiveHours() {
        return sameHoursEveryDay(9);
    }

    /** 하루 8시간씩 5일 — 주 합계 40시간. */
    public static List<TimeCard> eightHoursADay() {
        return sameHoursEveryDay(8);
    }

    private static List<TimeCard> sameHoursEveryDay(int hours) {
        return List.of(
                new TimeCard(MONDAY, hours),
                new TimeCard(MONDAY.plusDays(1), hours),
                new TimeCard(MONDAY.plusDays(2), hours),
                new TimeCard(MONDAY.plusDays(3), hours),
                new TimeCard(MONDAY.plusDays(4), hours)
        );
    }
}

package com.example.srp.ch07;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link Employee} 에서 <b>회계팀(CFO) 요구사항 하나만</b> 반영한 버전.
 *
 * <h2>회계팀의 요청</h2>
 * <blockquote>
 *     "연장수당은 주 40시간을 넘긴 시간에만 지급합니다.
 *      정규 근무시간 계산을 '하루 8시간' 기준이 아니라 '주 40시간' 기준으로 바꿔주세요."
 * </blockquote>
 *
 * <p>개발자는 요청대로 {@link #regularHours()} <b>딱 한 곳만</b> 수정했다.
 * {@link #calculatePay()}, {@link #reportHours()}, {@link #save()} 본문은
 * {@link Employee} 와 글자 하나 다르지 않다. (직접 비교해 보자.)
 *
 * <p>테스트를 돌려보면 요청하지도 않은 인사팀 보고서까지 바뀌어 있다.
 * 이것이 7장에서 말하는 <b>우발적 중복(Accidental Duplication)</b> 의 결과다.
 *
 * <p>※ 비교 실험을 위해 일부러 클래스를 통째로 복사해 둔 것이다.
 *    실제 프로젝트에서 이렇게 하라는 뜻은 아니다.
 */
public class EmployeeAfterCfoRequest {

    public static final long REGULAR_RATE = 20_000;
    public static final long OVERTIME_RATE = 30_000;

    /** ★ 변경점: 하루 8시간 → 주 40시간 기준. */
    private static final int REGULAR_HOURS_PER_WEEK = 40;

    private static final Map<String, EmployeeAfterCfoRequest> DATABASE = new HashMap<>();

    private final String name;
    private final List<TimeCard> timeCards;

    public EmployeeAfterCfoRequest(String name, List<TimeCard> timeCards) {
        this.name = name;
        this.timeCards = List.copyOf(timeCards);
    }

    // 액터 1: CFO (회계팀) — 본문은 원본과 동일
    public long calculatePay() {
        long regularHours = regularHours();
        long overtimeHours = totalHours() - regularHours;
        return regularHours * REGULAR_RATE + overtimeHours * OVERTIME_RATE;
    }

    // 액터 2: COO (인사팀) — 본문은 원본과 동일. 인사팀은 아무 요청도 하지 않았다.
    public String reportHours() {
        long regularHours = regularHours();
        long overtimeHours = totalHours() - regularHours;
        return """
                [근무시간 보고서] %s
                정규 근무시간: %d시간
                연장 근무시간: %d시간""".formatted(name, regularHours, overtimeHours);
    }

    // 액터 3: CTO (DBA) — 본문은 원본과 동일
    public void save() {
        DATABASE.put(name, this);
    }

    /**
     * ★ <b>여기 한 곳만 바뀌었다.</b>
     *
     * <p>원본: {@code 하루 8시간}을 넘긴 시간을 제외
     * <br>변경: {@code 주 40시간}을 넘긴 시간을 제외
     */
    private long regularHours() {
        return Math.min(totalHours(), REGULAR_HOURS_PER_WEEK);
    }

    private long totalHours() {
        return timeCards.stream().mapToLong(TimeCard::hours).sum();
    }

    public String name() {
        return name;
    }
}

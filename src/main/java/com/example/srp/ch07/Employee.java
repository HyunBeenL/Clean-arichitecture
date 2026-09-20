package com.example.srp.ch07;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 『클린 아키텍처』 7장에 나오는 {@code Employee} 클래스. (변경 전 원본)
 *
 * <p>메서드 세 개가 각각 <b>다른 액터</b>를 위해 존재한다.
 * <table border="1">
 *     <caption>액터</caption>
 *     <tr><th>메서드</th><th>요구한 부서</th><th>보고 대상(액터)</th></tr>
 *     <tr><td>{@link #calculatePay()}</td><td>회계팀</td><td>CFO</td></tr>
 *     <tr><td>{@link #reportHours()}</td><td>인사팀</td><td>COO</td></tr>
 *     <tr><td>{@link #save()}</td><td>DBA</td><td>CTO</td></tr>
 * </table>
 *
 * <p>문제는 {@link #calculatePay()} 와 {@link #reportHours()} 가
 * {@link #regularHours()} 라는 <b>하나의 함수를 공유</b>한다는 점이다.
 * 지금은 두 부서의 계산 규칙이 <b>우연히 같아서</b> 하나로 합쳐져 있을 뿐이다.
 */
public class Employee {

    /** 정규 근무 시급. */
    public static final long REGULAR_RATE = 20_000;
    /** 연장 근무 시급(1.5배). */
    public static final long OVERTIME_RATE = 30_000;

    /** 하루 정규 근무시간 한도. */
    private static final int REGULAR_HOURS_PER_DAY = 8;

    /** DB 대신 쓰는 임시 저장소. */
    private static final Map<String, Employee> DATABASE = new HashMap<>();

    private final String name;
    private final List<TimeCard> timeCards;

    public Employee(String name, List<TimeCard> timeCards) {
        this.name = name;
        this.timeCards = List.copyOf(timeCards);
    }

    // ============================================================
    // 액터 1: CFO (회계팀) — "급여를 계산해 주세요"
    // ============================================================
    public long calculatePay() {
        long regularHours = regularHours();
        long overtimeHours = totalHours() - regularHours;
        return regularHours * REGULAR_RATE + overtimeHours * OVERTIME_RATE;
    }

    // ============================================================
    // 액터 2: COO (인사팀) — "근무시간 보고서를 만들어 주세요"
    // ============================================================
    public String reportHours() {
        long regularHours = regularHours();
        long overtimeHours = totalHours() - regularHours;
        return """
                [근무시간 보고서] %s
                정규 근무시간: %d시간
                연장 근무시간: %d시간""".formatted(name, regularHours, overtimeHours);
    }

    // ============================================================
    // 액터 3: CTO (DBA) — "이 형식으로 저장해 주세요"
    // ============================================================
    public void save() {
        DATABASE.put(name, this);
    }

    /**
     * ⚠️ <b>문제의 함수.</b> 액터가 서로 다른 두 메서드가 이 함수를 함께 쓴다.
     *
     * <p>현재 규칙: 하루 8시간을 넘긴 시간은 정규 근무시간에서 제외한다.
     *
     * <p>회계팀이 "이 계산 방식을 바꿔달라"고 요청하면 개발자는 이 함수를 고칠 것이다.
     * 그런데 이 함수는 인사팀 보고서도 함께 쓰고 있다.
     * 인사팀은 아무것도 요청하지 않았는데 보고서 수치가 바뀌게 된다.
     */
    private long regularHours() {
        long regularHours = 0;
        for (TimeCard timeCard : timeCards) {
            regularHours += Math.min(timeCard.hours(), REGULAR_HOURS_PER_DAY);
        }
        return regularHours;
    }

    private long totalHours() {
        return timeCards.stream().mapToLong(TimeCard::hours).sum();
    }

    public String name() {
        return name;
    }
}

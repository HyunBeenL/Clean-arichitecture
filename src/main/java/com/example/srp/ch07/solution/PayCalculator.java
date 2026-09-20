package com.example.srp.ch07.solution;

import com.example.srp.ch07.TimeCard;

/**
 * 액터: <b>CFO (회계팀)</b> — 오직 이 부서만 이 클래스의 변경을 요청할 수 있다.
 *
 * <h2>회계팀이 정의하는 "정규 근무시간"</h2>
 * 연장수당을 지급하지 않아도 되는 시간. <b>주 40시간</b> 기준.
 * (problem 패키지에서 회계팀이 요청했던 바로 그 규칙이 여기 들어와 있다.)
 *
 * <p>이 클래스를 아무리 고쳐도 {@link HourReporter} 는 영향을 받지 않는다.
 * 두 클래스는 서로를 참조하지 않기 때문이다.
 */
public class PayCalculator {

    /** 정규 근무 시급. */
    public static final long REGULAR_RATE = 20_000;
    /** 연장 근무 시급(1.5배). */
    public static final long OVERTIME_RATE = 30_000;

    /** 회계팀 기준: 주 40시간. */
    private static final int REGULAR_HOURS_PER_WEEK = 40;

    public long calculatePay(EmployeeData employee) {
        long totalHours = totalHours(employee);
        long regularHours = regularHours(employee);
        long overtimeHours = totalHours - regularHours;
        return regularHours * REGULAR_RATE + overtimeHours * OVERTIME_RATE;
    }

    /** 회계팀의 정규 근무시간 규칙. 회계팀 말고는 아무도 이 함수를 쓰지 않는다. */
    private long regularHours(EmployeeData employee) {
        return Math.min(totalHours(employee), REGULAR_HOURS_PER_WEEK);
    }

    private long totalHours(EmployeeData employee) {
        return employee.timeCards().stream().mapToLong(TimeCard::hours).sum();
    }
}

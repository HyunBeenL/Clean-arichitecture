package com.example.srp.ch07.solution;

import com.example.srp.ch07.TimeCard;

/**
 * 액터: <b>COO (인사팀)</b> — 오직 이 부서만 이 클래스의 변경을 요청할 수 있다.
 *
 * <h2>인사팀이 정의하는 "정규 근무시간"</h2>
 * 근로기준법상 연장근로가 아닌 시간. <b>하루 8시간</b> 기준.
 * 회계팀 사정과는 아무 상관이 없다.
 *
 * <p>problem 패키지에서는 이 규칙이 회계팀 요청 한 건에 조용히 휩쓸렸다.
 * 이제는 이 파일을 열지 않는 한 바뀌지 않는다.
 */
public class HourReporter {

    /** 인사팀 기준: 하루 8시간. */
    private static final int REGULAR_HOURS_PER_DAY = 8;

    public String reportHours(EmployeeData employee) {
        long totalHours = totalHours(employee);
        long regularHours = regularHours(employee);
        long overtimeHours = totalHours - regularHours;
        return """
                [근무시간 보고서] %s
                정규 근무시간: %d시간
                연장 근무시간: %d시간""".formatted(employee.name(), regularHours, overtimeHours);
    }

    /** 인사팀의 정규 근무시간 규칙. 인사팀 말고는 아무도 이 함수를 쓰지 않는다. */
    private long regularHours(EmployeeData employee) {
        long regularHours = 0;
        for (TimeCard timeCard : employee.timeCards()) {
            regularHours += Math.min(timeCard.hours(), REGULAR_HOURS_PER_DAY);
        }
        return regularHours;
    }

    private long totalHours(EmployeeData employee) {
        return employee.timeCards().stream().mapToLong(TimeCard::hours).sum();
    }
}

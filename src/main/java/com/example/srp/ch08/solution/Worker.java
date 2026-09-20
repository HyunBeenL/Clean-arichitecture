package com.example.srp.ch08.solution;

/**
 * 급여 계산 대상.
 *
 * <p>problem 의 {@code Worker} 는 {@code EmploymentType} enum 을 들고 있었고,
 * 계산기가 그 값을 보고 분기했다. 여기서는 <b>고용형태 대신 정책 자체</b>를 들고 있다.
 * 계산기는 정책에게 물어보기만 하면 되므로 분기할 일이 없다.
 *
 * @param name          이름
 * @param payrollPolicy 이 직원에게 적용할 급여 정책
 * @param weeklyHours   주간 근무시간
 */
public record Worker(String name, PayrollPolicy payrollPolicy, long weeklyHours) {

    public Worker {
        if (weeklyHours < 0) {
            throw new IllegalArgumentException("근무시간은 0 이상이어야 합니다.");
        }
    }
}

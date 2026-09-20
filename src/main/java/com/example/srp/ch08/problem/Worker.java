package com.example.srp.ch08.problem;

/**
 * 급여 계산 대상. 한 주 동안 일한 시간만 들고 있다.
 *
 * @param name         이름
 * @param employmentType 고용형태
 * @param weeklyHours  주간 근무시간
 */
public record Worker(String name, EmploymentType employmentType, long weeklyHours) {

    public Worker {
        if (weeklyHours < 0) {
            throw new IllegalArgumentException("근무시간은 0 이상이어야 합니다.");
        }
    }
}

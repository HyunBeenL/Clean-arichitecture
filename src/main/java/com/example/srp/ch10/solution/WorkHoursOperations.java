package com.example.srp.ch10.solution;

/** {@link OvertimeMonitor} 가 필요로 하는 것. 책의 {@code U2Ops}. */
@FunctionalInterface
public interface WorkHoursOperations {

    /** 이번 주 근무시간. */
    long weeklyHours(String employeeId);
}

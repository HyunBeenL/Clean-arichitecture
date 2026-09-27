package com.example.srp.ch10.problem;

import java.util.List;

/**
 * 주 52시간 초과 근무 감시. <b>User2</b>.
 *
 * <p>쓰는 메서드: {@code weeklyHours()} 하나.
 * 의존하는 메서드: {@link EmployeeOperations} 의 세 개 전부.
 */
public class OvertimeMonitor {

    public static final long WEEKLY_LIMIT = 52;

    private final EmployeeOperations employees;

    public OvertimeMonitor(EmployeeOperations employees) {
        this.employees = employees;
    }

    /** 주 52시간을 넘겨 일한 사원 id 목록. */
    public List<String> findOverworked(List<String> employeeIds) {
        return employeeIds.stream()
                .filter(id -> employees.weeklyHours(id) > WEEKLY_LIMIT)
                .toList();
    }
}

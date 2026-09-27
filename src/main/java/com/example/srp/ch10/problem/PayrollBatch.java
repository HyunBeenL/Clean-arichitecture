package com.example.srp.ch10.problem;

import java.util.List;

/**
 * 주간 급여 지급 배치. <b>User1</b>.
 *
 * <p>쓰는 메서드: {@code calculatePay()} 하나.
 * 의존하는 메서드: {@link EmployeeOperations} 의 세 개 전부.
 */
public class PayrollBatch {

    private final EmployeeOperations employees;

    public PayrollBatch(EmployeeOperations employees) {
        this.employees = employees;
    }

    /** 이번 주 지급할 세후 급여 총액. */
    public long totalPay(List<String> employeeIds) {
        return employeeIds.stream().mapToLong(employees::calculatePay).sum();
    }
}

package com.example.srp.ch10.solution;

import java.util.List;

/**
 * 주간 급여 지급 배치. <b>User1</b>.
 *
 * <p>쓰는 메서드: {@code calculatePay()} 하나. 의존하는 메서드도 그 하나뿐이다.
 */
public class PayrollBatch {

    private final PayrollOperations payroll;

    public PayrollBatch(PayrollOperations payroll) {
        this.payroll = payroll;
    }

    /** 이번 주 지급할 세후 급여 총액. */
    public long totalPay(List<String> employeeIds) {
        return employeeIds.stream().mapToLong(payroll::calculatePay).sum();
    }
}

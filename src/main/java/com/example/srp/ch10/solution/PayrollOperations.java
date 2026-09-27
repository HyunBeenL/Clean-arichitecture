package com.example.srp.ch10.solution;

/**
 * {@link PayrollBatch} 가 필요로 하는 것. 책의 {@code U1Ops}.
 *
 * <pre>
 *  PayrollBatch    ──▶ PayrollOperations   (calculatePay) ─┐
 *  OvertimeMonitor ──▶ WorkHoursOperations (weeklyHours)  ─┼──▷ EmployeeService
 *  NoticeMailer    ──▶ EmployeeDirectory   (contactOf)    ─┘
 * </pre>
 *
 * <p>인터페이스를 <b>구현하는 쪽</b>이 아니라 <b>사용하는 쪽</b>을 기준으로 나눴다.
 * 이름도 사용하는 쪽의 언어로 지었다.
 */
@FunctionalInterface
public interface PayrollOperations {

    /** 이번 주 세후 급여. */
    long calculatePay(String employeeId);
}

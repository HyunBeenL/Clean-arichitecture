package com.example.srp.ch11.solution.payroll;

import com.example.srp.ch11.Employee;

/**
 * 급여명세서를 사원에게 전달한다. 메일인지 메신저인지는 업무 규칙이 알 바 아니다.
 */
@FunctionalInterface
public interface PayslipSender {

    void send(Employee employee, Payslip payslip);
}

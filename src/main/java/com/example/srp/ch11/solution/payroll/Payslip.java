package com.example.srp.ch11.solution.payroll;

/**
 * 급여명세서. PDF 인지 HTML 인지는 업무 규칙이 알 바 아니다.
 */
public interface Payslip {

    String fileName();

    byte[] content();
}

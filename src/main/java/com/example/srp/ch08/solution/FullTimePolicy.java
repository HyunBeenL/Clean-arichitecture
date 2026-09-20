package com.example.srp.ch08.solution;

/**
 * 정규직. 이 파일 하나만 보면 정규직의 급여 규칙을 전부 알 수 있다.
 */
public class FullTimePolicy implements PayrollPolicy {

    @Override
    public String code() {
        return "FULL_TIME";
    }

    @Override
    public String payslipTitle() {
        return "정규직 급여명세서";
    }

    @Override
    public long regularHoursLimit() {
        return 40;
    }

    @Override
    public long hourlyRate() {
        return 20_000;
    }

    @Override
    public long insuranceRate() {
        return 9;
    }
}

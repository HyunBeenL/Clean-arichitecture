package com.example.srp.ch08.solution;

/**
 * 계약직. 정규직과 기준이 다르지만, 그 차이가 이 파일 안에만 있다.
 */
public class ContractPolicy implements PayrollPolicy {

    @Override
    public String code() {
        return "CONTRACT";
    }

    @Override
    public String payslipTitle() {
        return "계약직 급여명세서";
    }

    @Override
    public long regularHoursLimit() {
        return 35;
    }

    @Override
    public long hourlyRate() {
        return 18_000;
    }

    @Override
    public long insuranceRate() {
        return 9;
    }
}

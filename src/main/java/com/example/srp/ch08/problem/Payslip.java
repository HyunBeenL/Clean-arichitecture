package com.example.srp.ch08.problem;

/**
 * 급여 계산 결과.
 *
 * @param workerName     이름
 * @param employmentType 고용형태
 * @param grossPay       공제 전 급여
 * @param deduction      4대보험 공제액
 * @param netPay         실수령액
 */
public record Payslip(
        String workerName,
        EmploymentType employmentType,
        long grossPay,
        long deduction,
        long netPay
) {
}

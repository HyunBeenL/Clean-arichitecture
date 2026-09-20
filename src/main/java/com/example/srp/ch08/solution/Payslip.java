package com.example.srp.ch08.solution;

/**
 * 급여 계산 결과.
 *
 * <p>명세서 제목까지 여기 담아둔다. 덕분에 {@link PayslipPrinter} 는
 * 고용형태가 무엇인지 알 필요가 없다.
 *
 * @param workerName   이름
 * @param payslipTitle 명세서 제목
 * @param grossPay     공제 전 급여
 * @param deduction    4대보험 공제액
 * @param netPay       실수령액
 */
public record Payslip(
        String workerName,
        String payslipTitle,
        long grossPay,
        long deduction,
        long netPay
) {
}

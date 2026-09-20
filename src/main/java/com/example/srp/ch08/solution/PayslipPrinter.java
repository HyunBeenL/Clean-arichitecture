package com.example.srp.ch08.solution;

/**
 * 급여명세서 출력. <b>여기에도 고용형태 분기가 없다.</b>
 *
 * <p>제목은 이미 {@link Payslip} 에 담겨 있으므로 그대로 찍기만 하면 된다.
 */
public class PayslipPrinter {

    public String print(Payslip payslip) {
        return """
                [%s]
                이름: %s
                지급총액: %,d원
                공제액: %,d원
                실수령액: %,d원""".formatted(
                payslip.payslipTitle(),
                payslip.workerName(),
                payslip.grossPay(),
                payslip.deduction(),
                payslip.netPay());
    }
}

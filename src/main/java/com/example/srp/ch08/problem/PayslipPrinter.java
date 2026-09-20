package com.example.srp.ch08.problem;

/**
 * ⚠️ 급여명세서 출력. <b>여기에도 고용형태 분기가 있다.</b>
 *
 * <p>고용형태를 하나 추가하려면 {@link PayrollCalculator} 뿐 아니라
 * 이 파일도 열어야 한다. 이렇게 하나의 변경이 여러 파일로 흩어지는 것을
 * <b>산탄총 수술(Shotgun Surgery)</b> 이라고 부른다.
 */
public class PayslipPrinter {

    public String print(Payslip payslip) {
        return """
                [%s]
                이름: %s
                지급총액: %,d원
                공제액: %,d원
                실수령액: %,d원""".formatted(
                title(payslip.employmentType()),
                payslip.workerName(),
                payslip.grossPay(),
                payslip.deduction(),
                payslip.netPay());
    }

    /** 분기 지점 ④ 명세서 제목. */
    private String title(EmploymentType type) {
        if (type == EmploymentType.FULL_TIME) {
            return "정규직 급여명세서";
        } else if (type == EmploymentType.CONTRACT) {
            return "계약직 급여명세서";
        }
        // ⚠️ 시간제 분기를 빠뜨렸다. 시간제 직원에게 "정규직 급여명세서"가 나간다.
        return "정규직 급여명세서";
    }
}

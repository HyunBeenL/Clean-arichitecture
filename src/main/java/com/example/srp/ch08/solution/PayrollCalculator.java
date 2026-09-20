package com.example.srp.ch08.solution;

/**
 * 급여 계산기. <b>고용형태 분기가 한 개도 없다.</b>
 *
 * <p>고용형태가 3종이든 30종이든 이 파일은 그대로다.
 * 계산 절차(정규/연장 시간을 나누고, 시급을 곱하고, 공제한다)만 알고 있고
 * 구체적인 숫자는 전부 {@link PayrollPolicy} 에게 물어본다.
 *
 * <p>problem 의 같은 이름 클래스와 나란히 놓고 비교해보자.
 * 거기에는 {@code if (type == ...)} 가 7곳 있었다.
 */
public class PayrollCalculator {

    public Payslip calculate(Worker worker) {
        PayrollPolicy policy = worker.payrollPolicy();

        long regularHours = Math.min(worker.weeklyHours(), policy.regularHoursLimit());
        long overtimeHours = worker.weeklyHours() - regularHours;

        long grossPay = regularHours * policy.hourlyRate() + overtimeHours * policy.overtimeRate();
        long deduction = grossPay * policy.insuranceRate() / 100;

        return new Payslip(
                worker.name(),
                policy.payslipTitle(),
                grossPay,
                deduction,
                grossPay - deduction);
    }
}

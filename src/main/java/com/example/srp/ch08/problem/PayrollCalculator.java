package com.example.srp.ch08.problem;

/**
 * ⚠️ OCP(개방-폐쇄 원칙)를 위반한 급여 계산기.
 *
 * <p>고용형태가 하나 늘어날 때마다 <b>이 파일을 열어서</b> 분기를 추가해야 한다.
 * 즉 확장에는 열려 있지만 <b>변경에도 열려 있다.</b>
 *
 * <h2>회계팀이 정의한 고용형태별 규칙</h2>
 * <table border="1">
 *     <caption>급여 규칙</caption>
 *     <tr><th></th><th>정규 근무시간 기준</th><th>시급</th><th>4대보험 공제</th></tr>
 *     <tr><td>정규직</td><td>주 40시간</td><td>20,000원</td><td>9% 공제</td></tr>
 *     <tr><td>계약직</td><td>주 35시간</td><td>18,000원</td><td>9% 공제</td></tr>
 *     <tr><td>시간제</td><td>제한 없음(전부 정규)</td><td>15,000원</td><td>공제 없음</td></tr>
 * </table>
 *
 * <p>연장 근무는 시급의 1.5배로 계산한다.
 *
 * <h2>지금 이 파일의 상태</h2>
 * 개발자가 시간제를 추가하려고 분기를 고치기 시작했는데,
 * 분기가 여기저기 흩어져 있어서 <b>일부를 빠뜨렸다.</b>
 * 그런데도 컴파일은 통과한다. 어디를 빠뜨렸는지 직접 찾아보자.
 */
public class PayrollCalculator {

    private static final long INSURANCE_RATE = 9;

    public Payslip calculate(Worker worker) {
        EmploymentType type = worker.employmentType();

        long limit = regularHoursLimit(type);
        long regularHours = Math.min(worker.weeklyHours(), limit);
        long overtimeHours = worker.weeklyHours() - regularHours;

        long rate = hourlyRate(type);
        long grossPay = regularHours * rate + overtimeHours * (rate * 3 / 2);
        long deduction = insuranceDeduction(type, grossPay);

        return new Payslip(worker.name(), type, grossPay, deduction, grossPay - deduction);
    }

    /** 분기 지점 ① 정규 근무시간 기준. */
    private long regularHoursLimit(EmploymentType type) {
        if (type == EmploymentType.FULL_TIME) {
            return 40;
        } else if (type == EmploymentType.CONTRACT) {
            return 35;
        }
        // ⚠️ 시간제는 "제한 없음"이어야 하는데 이 분기를 추가하지 않았다.
        //    컴파일러는 아무 말도 하지 않는다. 그냥 정규직과 같은 40시간이 적용된다.
        return 40;
    }

    /** 분기 지점 ② 시급. */
    private long hourlyRate(EmploymentType type) {
        if (type == EmploymentType.FULL_TIME) {
            return 20_000;
        } else if (type == EmploymentType.CONTRACT) {
            return 18_000;
        } else if (type == EmploymentType.PART_TIME) {
            return 15_000;   // ← 여기는 기억하고 고쳤다
        }
        return 20_000;
    }

    /** 분기 지점 ③ 4대보험 공제. */
    private long insuranceDeduction(EmploymentType type, long grossPay) {
        if (type == EmploymentType.FULL_TIME) {
            return grossPay * INSURANCE_RATE / 100;
        } else if (type == EmploymentType.CONTRACT) {
            return grossPay * INSURANCE_RATE / 100;
        }
        // ⚠️ 시간제는 4대보험 공제 대상이 아닌데 이 분기도 빠뜨렸다.
        return grossPay * INSURANCE_RATE / 100;
    }
}

package com.example.srp.ch08.problem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 기존 고용형태(정규직·계약직)는 잘 동작한다.
 *
 * <p>여기까지만 보면 아무 문제가 없어 보인다. 문제는 세 번째 고용형태를 추가할 때
 * 드러난다. → {@link OcpViolationTest}
 */
@DisplayName("ch08 problem · PayrollCalculator")
class PayrollCalculatorTest {

    /** 주 45시간 일한 경우로 통일해서 비교한다. */
    private static final long WEEKLY_HOURS = 45;

    private final PayrollCalculator payrollCalculator = new PayrollCalculator();

    @Nested
    @DisplayName("정규직 (주 40시간 기준 / 시급 20,000원 / 9% 공제)")
    class FullTime {

        private final Payslip payslip =
                new PayrollCalculator().calculate(new Worker("김정규", EmploymentType.FULL_TIME, WEEKLY_HOURS));

        @Test
        @DisplayName("정규 40시간 + 연장 5시간 → 지급총액 950,000원")
        void grossPay() {
            // 40 × 20,000 + 5 × 30,000
            assertThat(payslip.grossPay()).isEqualTo(950_000);
        }

        @Test
        @DisplayName("9% 공제 후 실수령액 864,500원")
        void netPay() {
            assertThat(payslip.deduction()).isEqualTo(85_500);
            assertThat(payslip.netPay()).isEqualTo(864_500);
        }
    }

    @Nested
    @DisplayName("계약직 (주 35시간 기준 / 시급 18,000원 / 9% 공제)")
    class Contract {

        private final Payslip payslip =
                new PayrollCalculator().calculate(new Worker("이계약", EmploymentType.CONTRACT, WEEKLY_HOURS));

        @Test
        @DisplayName("정규 35시간 + 연장 10시간 → 지급총액 900,000원")
        void grossPay() {
            // 35 × 18,000 + 10 × 27,000
            assertThat(payslip.grossPay()).isEqualTo(900_000);
        }

        @Test
        @DisplayName("9% 공제 후 실수령액 819,000원")
        void netPay() {
            assertThat(payslip.deduction()).isEqualTo(81_000);
            assertThat(payslip.netPay()).isEqualTo(819_000);
        }
    }

    @Test
    @DisplayName("명세서에는 고용형태에 맞는 제목이 찍힌다")
    void payslipTitle() {
        PayslipPrinter printer = new PayslipPrinter();

        assertThat(printer.print(payrollCalculator.calculate(
                new Worker("김정규", EmploymentType.FULL_TIME, WEEKLY_HOURS))))
                .contains("[정규직 급여명세서]");

        assertThat(printer.print(payrollCalculator.calculate(
                new Worker("이계약", EmploymentType.CONTRACT, WEEKLY_HOURS))))
                .contains("[계약직 급여명세서]");
    }
}

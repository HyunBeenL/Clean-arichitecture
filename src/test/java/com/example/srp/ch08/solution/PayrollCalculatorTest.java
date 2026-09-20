package com.example.srp.ch08.solution;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 세 고용형태가 모두 회계팀 명세대로 동작한다.
 *
 * <p>특히 {@link PartTime} 을 주목하자. problem 에서 분기를 빠뜨려 깨졌던 바로 그 값들이다.
 */
@DisplayName("ch08 solution · PayrollCalculator")
class PayrollCalculatorTest {

    private static final long WEEKLY_HOURS = 45;

    private final PayrollCalculator payrollCalculator = new PayrollCalculator();
    private final PayslipPrinter payslipPrinter = new PayslipPrinter();

    @Nested
    @DisplayName("정규직 (주 40시간 기준 / 시급 20,000원 / 9% 공제)")
    class FullTime {

        private final Payslip payslip = new PayrollCalculator()
                .calculate(new Worker("김정규", new FullTimePolicy(), WEEKLY_HOURS));

        @Test
        @DisplayName("정규 40시간 + 연장 5시간 → 950,000원, 공제 후 864,500원")
        void pay() {
            assertThat(payslip.grossPay()).isEqualTo(950_000);
            assertThat(payslip.deduction()).isEqualTo(85_500);
            assertThat(payslip.netPay()).isEqualTo(864_500);
        }
    }

    @Nested
    @DisplayName("계약직 (주 35시간 기준 / 시급 18,000원 / 9% 공제)")
    class Contract {

        private final Payslip payslip = new PayrollCalculator()
                .calculate(new Worker("이계약", new ContractPolicy(), WEEKLY_HOURS));

        @Test
        @DisplayName("정규 35시간 + 연장 10시간 → 900,000원, 공제 후 819,000원")
        void pay() {
            assertThat(payslip.grossPay()).isEqualTo(900_000);
            assertThat(payslip.deduction()).isEqualTo(81_000);
            assertThat(payslip.netPay()).isEqualTo(819_000);
        }
    }

    @Nested
    @DisplayName("시간제 (제한 없음 / 시급 15,000원 / 공제 없음) - problem 에서 깨졌던 부분")
    class PartTime {

        private final Payslip payslip = new PayrollCalculator()
                .calculate(new Worker("박시간", new PartTimePolicy(), WEEKLY_HOURS));

        @Test
        @DisplayName("연장수당 없이 45시간 전부 정규 근무 → 675,000원")
        void noOvertime() {
            assertThat(payslip.grossPay()).isEqualTo(675_000);
        }

        @Test
        @DisplayName("4대보험 공제 대상이 아니므로 실수령액도 675,000원")
        void noDeduction() {
            assertThat(payslip.deduction()).isZero();
            assertThat(payslip.netPay()).isEqualTo(675_000);
        }

        @Test
        @DisplayName("명세서 제목은 '시간제 급여명세서'")
        void title() {
            assertThat(new PayslipPrinter().print(payslip)).contains("[시간제 급여명세서]");
        }
    }

    @Test
    @DisplayName("명세서 제목은 정책이 정한 대로 찍힌다")
    void payslipTitles() {
        assertThat(payslipPrinter.print(payrollCalculator.calculate(
                new Worker("김정규", new FullTimePolicy(), WEEKLY_HOURS))))
                .contains("[정규직 급여명세서]");

        assertThat(payslipPrinter.print(payrollCalculator.calculate(
                new Worker("이계약", new ContractPolicy(), WEEKLY_HOURS))))
                .contains("[계약직 급여명세서]");
    }
}

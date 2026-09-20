package com.example.srp.ch07;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 변경 전 {@link Employee} 의 동작.
 *
 * <p>두 액터(회계팀/인사팀)가 <b>같은 클래스</b>를 향해 각자의 기대를 갖고 있다.
 * 지금은 둘 다 통과한다. 문제는 이 다음이다. → {@link AccidentalDuplicationTest}
 */
@DisplayName("변경 전 Employee")
class EmployeeTest {

    private final Employee employee = new Employee("김개발", TimeCards.oneWeek());

    @Nested
    @DisplayName("액터 1 · 회계팀(CFO)의 기대")
    class Payroll {

        @Test
        @DisplayName("정규 28시간 + 연장 4시간 → 급여 680,000원")
        void calculatePay() {
            // 28시간 × 20,000원 = 560,000원
            //  4시간 × 30,000원 = 120,000원
            assertThat(employee.calculatePay()).isEqualTo(680_000);
        }
    }

    @Nested
    @DisplayName("액터 2 · 인사팀(COO)의 기대")
    class HrReport {

        @Test
        @DisplayName("하루 8시간을 넘긴 시간은 연장근로로 집계된다 → 정규 28시간 / 연장 4시간")
        void reportHours() {
            assertThat(employee.reportHours())
                    .contains("정규 근무시간: 28시간")
                    .contains("연장 근무시간: 4시간");
        }
    }
}

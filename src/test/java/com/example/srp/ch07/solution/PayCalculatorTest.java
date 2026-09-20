package com.example.srp.ch07.solution;

import com.example.srp.ch07.TimeCards;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 회계팀(CFO) 규칙만 검증한다.
 *
 * <p>인사팀 이야기는 이 파일 어디에도 나오지 않는다.
 * 회계팀 규칙이 바뀌면 <b>이 파일만</b> 고치면 된다.
 */
@DisplayName("solution · PayCalculator · 회계팀(CFO) 규칙")
class PayCalculatorTest {

    private final PayCalculator payCalculator = new PayCalculator();
    private final EmployeeData employee = new EmployeeData("김개발", TimeCards.oneWeek());

    @Test
    @DisplayName("주 40시간을 넘지 않으면 연장수당 없이 640,000원")
    void withinFortyHoursPerWeek() {
        // 주 32시간 전부 정규 근무 → 32 × 20,000원
        assertThat(payCalculator.calculatePay(employee)).isEqualTo(640_000);
    }

    @Test
    @DisplayName("주 40시간을 넘긴 시간에는 1.5배가 적용된다")
    void overFortyHoursPerWeek() {
        // 하루 9시간씩 5일 = 45시간 → 정규 40시간 + 연장 5시간
        EmployeeData hardWorker = new EmployeeData("이야근", TimeCards.fortyFiveHours());

        // 40 × 20,000 + 5 × 30,000 = 950,000원
        assertThat(payCalculator.calculatePay(hardWorker)).isEqualTo(950_000);
    }
}

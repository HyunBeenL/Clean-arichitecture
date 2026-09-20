package com.example.srp.ch07.solution;

import java.util.List;

import com.example.srp.ch07.TimeCard;
import com.example.srp.ch07.TimeCards;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <b>액터 격리(Actor Isolation)</b> 검증.
 *
 * <p>아래 두 가지가 <b>동시에</b> 성립한다. problem 패키지에서는 불가능했던 조합이다.
 * <ul>
 *     <li>급여 640,000원 — 회계팀이 요청한 '주 40시간' 규칙이 반영됨</li>
 *     <li>보고서 정규 28시간 / 연장 4시간 — 인사팀의 '하루 8시간' 기준 그대로</li>
 * </ul>
 */
@DisplayName("solution · 액터 격리 검증")
class ActorIsolationTest {

    private static final List<TimeCard> WEEK = TimeCards.oneWeek();

    private final EmployeeData employee = new EmployeeData("김개발", WEEK);
    private final PayCalculator payCalculator = new PayCalculator();
    private final HourReporter hourReporter = new HourReporter();

    @Test
    @DisplayName("1. 회계팀 요청(주 40시간 기준)이 급여에 반영되어 있다")
    void payFollowsCfoRule() {
        assertThat(payCalculator.calculatePay(employee)).isEqualTo(640_000);
    }

    @Test
    @DisplayName("2. [핵심] 그런데 인사팀 보고서는 하루 8시간 기준 그대로다")
    void reportIsUntouchedByCfoRule() {
        // problem 패키지에서는 여기가 32시간 / 0시간으로 깨졌다.
        assertThat(hourReporter.reportHours(employee))
                .contains("정규 근무시간: 28시간")
                .contains("연장 근무시간: 4시간");
    }

    @Test
    @DisplayName("3. 실제로 있었던 연장근로 4시간이 보고서에 그대로 남아 있다")
    void overtimeSurvivesInReport() {
        long hoursOverEightPerDay = WEEK.stream()
                .mapToLong(card -> Math.max(card.hours() - 8, 0))
                .sum();
        assertThat(hoursOverEightPerDay).isEqualTo(4);

        // problem 패키지에서는 이 4시간이 보고서에서 사라졌다.
        assertThat(hourReporter.reportHours(employee)).contains("연장 근무시간: 4시간");
    }
}

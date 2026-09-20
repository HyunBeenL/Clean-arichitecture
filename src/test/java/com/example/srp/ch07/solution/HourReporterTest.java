package com.example.srp.ch07.solution;

import com.example.srp.ch07.TimeCards;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 인사팀(COO) 규칙만 검증한다.
 *
 * <p>★ problem 패키지에서 회계팀 요청 때문에 깨졌던 바로 그 단언이다.
 * ({@code problem.HrReportRegressionTest} 와 같은 내용) 여기서는 깨지지 않는다.
 *
 * <p>확인해보고 싶다면: {@link PayCalculator} 의 {@code REGULAR_HOURS_PER_WEEK} 를
 * 35 든 20 이든 마음대로 바꾸고 전체 테스트를 돌려보자. 이 테스트는 그대로 통과한다.
 */
@DisplayName("solution · HourReporter · 인사팀(COO) 규칙")
class HourReporterTest {

    private final HourReporter hourReporter = new HourReporter();
    private final EmployeeData employee = new EmployeeData("김개발", TimeCards.oneWeek());

    @Test
    @DisplayName("하루 8시간을 넘긴 시간은 연장근로로 집계된다 → 정규 28시간 / 연장 4시간")
    void regularHoursFollowDailyLimit() {
        assertThat(hourReporter.reportHours(employee))
                .contains("정규 근무시간: 28시간")
                .contains("연장 근무시간: 4시간");
    }

    @Test
    @DisplayName("하루 8시간 이하로만 일했다면 연장근로는 0시간이다")
    void noOvertime() {
        EmployeeData regularWorker = new EmployeeData("박정시", TimeCards.eightHoursADay());

        assertThat(hourReporter.reportHours(regularWorker))
                .contains("정규 근무시간: 40시간")
                .contains("연장 근무시간: 0시간");
    }
}

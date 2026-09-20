package com.example.srp.ch07.problem;

import com.example.srp.ch07.TimeCards;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 인사팀(COO)이 예전에 작성해 둔 테스트.
 *
 * <p>인사팀 입장에서 "정규 근무시간"이란 근로기준법상 연장근로가 아닌 시간,
 * 즉 <b>하루 8시간</b> 이내의 시간이다. 이 기준은 회계팀 사정과 무관하게 그대로다.
 *
 * <p>아래 {@code @Disabled} 를 지우고 실행하면 <b>빨간불</b>이 뜬다.
 * 인사팀은 아무 변경도 요청하지 않았는데 테스트가 깨진 것이다.
 *
 * <p>그리고 진짜 무서운 질문: <b>인사팀에 이 테스트가 없었다면?</b>
 * 아무도 모르는 채로 잘못된 보고서가 몇 달간 그대로 나갔을 것이다.
 * 7장에서 COO가 뒤늦게 발견하고 분노하는 장면이 바로 이것이다.
 */
@Disabled("""
        ★ 이 @Disabled 한 줄을 지우고 실행해보세요. 테스트가 깨지는 것이 정상입니다.
           인사팀은 아무 변경도 요청하지 않았는데, 회계팀 요청 때문에 깨집니다.
        """)
@DisplayName("인사팀이 작성해 둔 테스트 (회계팀 변경 후)")
class HrReportRegressionTest {

    private final EmployeeAfterCfoRequest employee =
            new EmployeeAfterCfoRequest("김개발", TimeCards.oneWeek());

    @Test
    @DisplayName("인사팀 기준: 하루 8시간을 넘긴 시간은 연장근로로 집계되어야 한다")
    void regularHoursMustFollowDailyLimit() {
        assertThat(employee.reportHours())
                .contains("정규 근무시간: 28시간")
                .contains("연장 근무시간: 4시간");
    }
}

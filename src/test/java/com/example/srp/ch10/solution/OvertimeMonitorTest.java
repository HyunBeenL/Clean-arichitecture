package com.example.srp.ch10.solution;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * problem 의 같은 테스트와 비교해보자.
 * 저쪽은 가짜 객체가 메서드 셋을 구현하고 그중 둘을 예외로 막았다.
 * 여기서는 {@link WorkHoursOperations} 가 메서드 하나짜리라 <b>람다 한 줄</b>로 끝난다.
 * 급여 계산이 어떻게 바뀌든 이 파일은 영향을 받지 않는다.
 */
@DisplayName("ch10 solution · OvertimeMonitor")
class OvertimeMonitorTest {

    @Test
    @DisplayName("주 52시간을 넘긴 사원만 골라낸다 (52시간 정각은 초과가 아니다)")
    void findOverworked() {
        Map<String, Long> hours = Map.of("A", 40L, "B", 52L, "C", 53L);
        OvertimeMonitor monitor = new OvertimeMonitor(hours::get);

        assertThat(monitor.findOverworked(List.of("A", "B", "C"))).containsExactly("C");
    }
}

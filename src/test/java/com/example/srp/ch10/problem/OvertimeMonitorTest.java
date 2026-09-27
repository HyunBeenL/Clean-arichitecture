package com.example.srp.ch10.problem;

import java.util.List;
import java.util.Map;

import com.example.srp.ch10.Contact;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 초과근무 감시만 테스트하고 싶다. 필요한 건 "사원별 근무시간" 하나뿐이다.
 *
 * <p>그런데 {@link OvertimeMonitor} 가 {@link EmployeeOperations} 를 받으므로
 * 가짜 객체도 {@code calculatePay}, {@code contactOf} 까지 구현해야 한다.
 * 아래 {@link FakeWorkHours} 를 보자. 메서드 셋 중 둘은 이 테스트와 아무 상관이 없다.
 *
 * <p>그리고 회계팀이 {@code calculatePay} 시그니처를 바꾸면 <b>이 파일도 컴파일 에러가 난다.</b>
 * 근무시간 감시와 아무 관련 없는 변경인데도.
 */
@DisplayName("ch10 problem · OvertimeMonitor")
class OvertimeMonitorTest {

    private static final class FakeWorkHours implements EmployeeOperations {

        private final Map<String, Long> hours;

        FakeWorkHours(Map<String, Long> hours) {
            this.hours = hours;
        }

        @Override
        public long weeklyHours(String employeeId) {
            return hours.get(employeeId);
        }

        @Override
        public long calculatePay(String employeeId) {
            throw new UnsupportedOperationException("이 테스트와 상관없음");
        }

        @Override
        public Contact contactOf(String employeeId) {
            throw new UnsupportedOperationException("이 테스트와 상관없음");
        }
    }

    @Test
    @DisplayName("주 52시간을 넘긴 사원만 골라낸다 (52시간 정각은 초과가 아니다)")
    void findOverworked() {
        OvertimeMonitor monitor = new OvertimeMonitor(new FakeWorkHours(Map.of("A", 40L, "B", 52L, "C", 53L)));

        assertThat(monitor.findOverworked(List.of("A", "B", "C"))).containsExactly("C");
    }
}

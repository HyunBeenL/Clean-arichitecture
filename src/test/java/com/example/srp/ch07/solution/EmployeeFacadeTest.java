package com.example.srp.ch07.solution;

import com.example.srp.ch07.TimeCards;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 퍼사드가 세 클래스에 제대로 위임하는지만 확인한다.
 *
 * <p>퍼사드에는 업무 규칙이 없으므로 검증할 것도 이게 전부다.
 */
@DisplayName("solution · EmployeeFacade · 위임")
class EmployeeFacadeTest {

    private final EmployeeFacade employee = new EmployeeFacade("김개발", TimeCards.oneWeek());

    @Test
    @DisplayName("호출하는 쪽 코드는 problem 패키지와 똑같은 모양으로 유지된다")
    void delegatesToEachActor() {
        assertThat(employee.calculatePay()).isEqualTo(640_000);
        assertThat(employee.reportHours()).contains("정규 근무시간: 28시간");

        employee.save();
        assertThat(employee.employeeData().name()).isEqualTo("김개발");
    }
}

package com.example.srp.ch07.solution;

import com.example.srp.ch07.TimeCards;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** DBA(CTO) 규칙만 검증한다. */
@DisplayName("solution · EmployeeSaver · DBA(CTO) 규칙")
class EmployeeSaverTest {

    private final EmployeeSaver employeeSaver = new EmployeeSaver();

    @Test
    @DisplayName("저장한 직원을 이름으로 다시 찾을 수 있다")
    void saveAndFind() {
        EmployeeData employee = new EmployeeData("김개발", TimeCards.oneWeek());

        employeeSaver.save(employee);

        assertThat(employeeSaver.findByName("김개발")).contains(employee);
    }

    @Test
    @DisplayName("저장한 적 없는 직원은 조회되지 않는다 (테스트 간 상태 공유 없음)")
    void notFound() {
        assertThat(employeeSaver.findByName("김개발")).isEmpty();
    }
}

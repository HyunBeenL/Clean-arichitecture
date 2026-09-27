package com.example.srp.ch10.solution;

import java.util.List;

import com.example.srp.ch10.Employees;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ch10 solution · PayrollBatch")
class PayrollBatchTest {

    @Test
    @DisplayName("가짜 급여 계산은 람다 한 줄이면 된다")
    void totalPayWithLambda() {
        PayrollBatch batch = new PayrollBatch(id -> 100_000);

        assertThat(batch.totalPay(List.of("A", "B", "C"))).isEqualTo(300_000);
    }

    @Test
    @DisplayName("실제 구현으로도: 810,000 + 891,000 + 270,000 = 1,971,000원")
    void totalPayWithService() {
        PayrollBatch batch = new PayrollBatch(new EmployeeService(Employees.roster(), Employees.TAX_SERVER_UP));

        assertThat(batch.totalPay(Employees.ALL_IDS)).isEqualTo(1_971_000);
    }
}

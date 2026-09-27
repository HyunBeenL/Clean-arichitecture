package com.example.srp.ch10.problem;

import com.example.srp.ch10.Employees;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ch10 problem · PayrollBatch (세율표 서버가 정상일 때)")
class PayrollBatchTest {

    @Test
    @DisplayName("세후 급여 총액: 810,000 + 891,000 + 270,000 = 1,971,000원")
    void totalPay() {
        PayrollBatch batch = new PayrollBatch(new EmployeeService(Employees.roster(), Employees.TAX_SERVER_UP));

        assertThat(batch.totalPay(Employees.ALL_IDS)).isEqualTo(1_971_000);
    }
}

package com.example.srp.ch11.solution;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.example.srp.ch11.Employee;
import com.example.srp.ch11.solution.payroll.PayDay;
import com.example.srp.ch11.solution.payroll.Payslip;
import com.example.srp.ch11.solution.payroll.PayslipFactory;
import com.example.srp.ch11.solution.payroll.PayslipSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.example.srp.ch11.Employees.KIM;
import static com.example.srp.ch11.Employees.PARK;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * problem 의 {@code PayRuleGapTest} 가 쓰고 싶었던 테스트. 여기서는 그냥 쓸 수 있다.
 *
 * <p>DB 도, 메일 서버도, PDF 도 필요 없다. 세 인터페이스를 람다로 채우면 끝이다.
 */
@DisplayName("ch11 solution · PayDay 급여 규칙 (DB·메일 서버 없이)")
class PayDayTest {

    private record TextPayslip(String fileName, byte[] content) implements Payslip {
    }

    private final List<String> sent = new ArrayList<>();

    private final PayslipFactory textPayslips = (employee, pay) ->
            new TextPayslip(employee.name() + ".txt", String.valueOf(pay).getBytes(StandardCharsets.UTF_8));

    private final PayslipSender recordingSender = (employee, payslip) ->
            sent.add(employee.email() + " ← " + payslip.fileName());

    private PayDay payDayFor(Employee... employees) {
        return new PayDay(() -> List.of(employees), textPayslips, recordingSender);
    }

    @Test
    @DisplayName("주 40시간 이하는 시급 그대로: 20시간 × 15,000원 = 300,000원")
    void regularPay() {
        assertThat(payDayFor(PARK).run()).isEqualTo(300_000);
    }

    @Test
    @DisplayName("초과분은 1.5배: 40 × 20,000 + 5 × 30,000 = 950,000원")
    void overtimePay() {
        assertThat(payDayFor(KIM).run()).isEqualTo(950_000);
    }

    @Test
    @DisplayName("40시간 정각은 연장이 아니다: 40 × 20,000 = 800,000원")
    void exactlyFortyHours() {
        Employee fortyHours = new Employee("E9", "정시", "jeong@example.com", 20_000, 40);

        assertThat(payDayFor(fortyHours).run()).isEqualTo(800_000);
    }

    @Test
    @DisplayName("사원마다 명세서를 하나씩 만들어 보낸다")
    void sendsOnePayslipPerEmployee() {
        payDayFor(KIM, PARK).run();

        assertThat(sent).containsExactly(
                "kim@example.com ← 김개발.txt",
                "park@example.com ← 박시간.txt");
    }
}

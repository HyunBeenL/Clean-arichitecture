package com.example.srp.ch11.solution.main;

import com.example.srp.ch11.solution.infra.MySqlEmployeeRepository;
import com.example.srp.ch11.solution.infra.PdfPayslipFactory;
import com.example.srp.ch11.solution.infra.SmtpPayslipSender;
import com.example.srp.ch11.solution.payroll.PayDay;

/**
 * 조립 지점. 책이 말하는 <b>main</b> 이다.
 *
 * <p>구체 클래스의 이름을 전부 아는 <b>유일한 곳</b>이다.
 * 어딘가에서는 결국 {@code new MySqlEmployeeRepository()} 를 해야 하므로 DIP 위반을 완전히 없앨 수는 없다.
 * 책의 처방은 그 위반을 <b>소수의 구체 컴포넌트에 모아 격리하는 것</b>이다.
 *
 * <p>메일을 메신저로 바꾸거나 PDF 를 HTML 로 바꾸는 결정은 이 파일에서 한다.
 * 업무 규칙({@code payroll})은 열지 않는다.
 *
 * <p>스프링이라면 {@code @Configuration} 클래스나 컴포넌트 스캔이 이 역할을 한다.
 * (이 프로젝트는 스프링 부트 {@code main()} 이 이미 있어서, 두 번째 {@code main()} 대신 팩토리 메서드만 두었다.)
 */
public final class PayrollMain {

    private PayrollMain() {
    }

    public static PayDay createPayDay() {
        return new PayDay(
                new MySqlEmployeeRepository(),
                new PdfPayslipFactory(),
                new SmtpPayslipSender("smtp.company.com", 587));
    }
}

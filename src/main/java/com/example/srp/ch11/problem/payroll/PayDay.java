package com.example.srp.ch11.problem.payroll;

import com.example.srp.ch11.Employee;
import com.example.srp.ch11.problem.infra.MySqlEmployeeRepository;
import com.example.srp.ch11.problem.infra.PdfPayslip;
import com.example.srp.ch11.problem.infra.SmtpMailSender;

/**
 * ⚠️ DIP(의존성 역전 원칙)를 위반한 급여 지급 업무 규칙.
 *
 * <p>이 클래스는 <b>고수준 정책</b>이다. 회사가 존재하는 이유에 가까운 규칙,
 * "주 40시간까지는 시급, 초과분은 1.5배로 계산해 명세서를 보낸다"를 담고 있다.
 *
 * <p>그런데 소스 코드 의존성이 <b>세부사항 쪽을 향한다.</b>
 * <pre>
 *  payroll.PayDay ──import──▶ infra.MySqlEmployeeRepository
 *                 ──import──▶ infra.SmtpMailSender
 *                 ──import──▶ infra.PdfPayslip
 * </pre>
 *
 * <p>책이 말하는 실천법을 거의 전부 어겼다.
 * <ul>
 *     <li>변동성이 큰 구체 클래스를 참조한다. (필드 타입이 구체 클래스)</li>
 *     <li>구체 클래스를 직접 {@code new} 한다. (추상 팩토리가 없다)</li>
 *     <li>변동성이 큰 구체의 이름을 언급한다.</li>
 * </ul>
 *
 * <p>결과적으로
 * <ul>
 *     <li>알림 수단이나 명세서 형식 같은 <b>세부사항이 바뀌면 업무 규칙 파일을 고쳐야 한다.</b></li>
 *     <li>급여 규칙 하나를 확인하려 해도 <b>운영 DB 와 메일 서버가 있어야 한다.</b></li>
 * </ul>
 */
public class PayDay {

    private static final long REGULAR_HOURS = 40;

    private final MySqlEmployeeRepository repository = new MySqlEmployeeRepository();
    private final SmtpMailSender mailSender = new SmtpMailSender("smtp.company.com", 587);

    /** 모든 사원의 급여를 계산해 명세서를 보내고, 지급 총액을 돌려준다. */
    public long run() {
        long total = 0;
        for (Employee employee : repository.findAll()) {
            long pay = payFor(employee);
            PdfPayslip payslip = new PdfPayslip(employee.name(), pay);
            mailSender.send(employee.email(), payslip.fileName(), payslip.toBytes());
            total += pay;
        }
        return total;
    }

    /** 업무 규칙: 주 40시간까지는 시급, 초과분은 1.5배. */
    private long payFor(Employee employee) {
        long regularHours = Math.min(employee.weeklyHours(), REGULAR_HOURS);
        long overtimeHours = employee.weeklyHours() - regularHours;
        return regularHours * employee.hourlyRate() + overtimeHours * employee.hourlyRate() * 3 / 2;
    }
}

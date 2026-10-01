package com.example.srp.ch11.solution.payroll;

import com.example.srp.ch11.Employee;

/**
 * 급여 지급 업무 규칙. <b>이 패키지 밖의 변동성 큰 구체를 하나도 모른다.</b>
 *
 * <p>업무 규칙({@code payFor}, {@code run}) 코드는 problem 과 똑같다.
 * 달라진 건 필드 타입과 생성자뿐이다.
 * <pre>
 *  problem                                  solution
 *  ─────────────────────────────────────    ─────────────────────────────
 *  MySqlEmployeeRepository repository       EmployeeRepository employees
 *      = new MySqlEmployeeRepository();       ← 생성자로 받는다
 *  new PdfPayslip(...)                      payslips.makePayslip(...)
 *  SmtpMailSender mailSender                PayslipSender sender
 * </pre>
 *
 * <p>의존하는 세 인터페이스는 모두 <b>이 패키지 안에</b> 있다.
 * 세부사항({@code infra})이 이 패키지를 향해 의존한다. 반대 방향은 없다.
 */
public class PayDay {

    private static final long REGULAR_HOURS = 40;

    private final EmployeeRepository employees;
    private final PayslipFactory payslips;
    private final PayslipSender sender;

    public PayDay(EmployeeRepository employees, PayslipFactory payslips, PayslipSender sender) {
        this.employees = employees;
        this.payslips = payslips;
        this.sender = sender;
    }

    /** 모든 사원의 급여를 계산해 명세서를 보내고, 지급 총액을 돌려준다. */
    public long run() {
        long total = 0;
        for (Employee employee : employees.findAll()) {
            long pay = payFor(employee);
            Payslip payslip = payslips.makePayslip(employee, pay);
            sender.send(employee, payslip);
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

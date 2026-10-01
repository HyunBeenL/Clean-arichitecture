package com.example.srp.ch11.solution.infra;

import com.example.srp.ch11.Employee;
import com.example.srp.ch11.solution.payroll.Payslip;
import com.example.srp.ch11.solution.payroll.PayslipSender;

/**
 * SMTP 메일로 급여명세서를 보낸다. <b>세부사항</b>이다.
 *
 * <p>실제라면 메일 서버에 접속한다. 이 예제 환경에는 메일 서버가 없으므로 전송이 실패한다.
 */
public class SmtpPayslipSender implements PayslipSender {

    private final String host;
    private final int port;

    public SmtpPayslipSender(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void send(Employee employee, Payslip payslip) {
        throw new IllegalStateException("SMTP 서버에 연결할 수 없습니다: " + host + ":" + port);
    }
}

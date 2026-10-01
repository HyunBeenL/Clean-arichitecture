package com.example.srp.ch11.solution.infra;

import com.example.srp.ch11.Employee;
import com.example.srp.ch11.solution.payroll.Payslip;
import com.example.srp.ch11.solution.payroll.PayslipFactory;

/**
 * PDF 명세서를 만드는 팩토리. 책 그림 11.1 의 {@code ServiceFactoryImpl} 에 해당한다.
 *
 * <p>{@code new PdfPayslip(...)} 은 이제 업무 규칙이 아니라 <b>여기</b>에 있다.
 * 구체를 만드는 코드가 구체 쪽으로 옮겨 왔다.
 */
public class PdfPayslipFactory implements PayslipFactory {

    @Override
    public Payslip makePayslip(Employee employee, long pay) {
        return new PdfPayslip(employee.name(), pay);
    }
}

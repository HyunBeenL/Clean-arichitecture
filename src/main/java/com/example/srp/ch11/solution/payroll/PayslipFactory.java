package com.example.srp.ch11.solution.payroll;

import com.example.srp.ch11.Employee;

/**
 * 급여명세서를 만든다. 책 그림 11.1 의 {@code ServiceFactory} 에 해당하는 <b>추상 팩토리</b>다.
 *
 * <p>업무 규칙은 사원마다 명세서를 <b>새로 만들어야</b> 한다.
 * 인터페이스로 받는 것만으로는 부족하다. {@code new PdfPayslip(...)} 을 쓰는 순간
 * 구체 클래스의 이름을 언급하게 되기 때문이다.
 * 그래서 생성 자체를 이 인터페이스 뒤로 숨긴다.
 */
@FunctionalInterface
public interface PayslipFactory {

    Payslip makePayslip(Employee employee, long pay);
}

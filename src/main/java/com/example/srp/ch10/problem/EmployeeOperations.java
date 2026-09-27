package com.example.srp.ch10.problem;

import com.example.srp.ch10.Contact;

/**
 * ⚠️ 뚱뚱한 인터페이스. 책 10장의 {@code OPS} 에 해당한다.
 *
 * <pre>
 *  PayrollBatch    ──┐
 *  OvertimeMonitor ──┼──▶ EmployeeOperations
 *  NoticeMailer    ──┘      calculatePay()  ← PayrollBatch 만 쓴다
 *                           weeklyHours()   ← OvertimeMonitor 만 쓴다
 *                           contactOf()     ← NoticeMailer 만 쓴다
 * </pre>
 *
 * <p>세 사용자는 각자 메서드를 <b>하나씩만</b> 호출한다.
 * 그런데 소스 코드상으로는 <b>셋 모두에 의존한다.</b> 파라미터 타입이 이 인터페이스이기 때문이다.
 *
 * <p>그래서
 * <ul>
 *     <li>회계팀 사정으로 {@code calculatePay} 가 바뀌면 메일 발송 쪽 코드와 테스트까지 영향을 받는다.</li>
 *     <li>공지 메일만 보내고 싶어도 급여 계산까지 할 줄 아는 객체를 구해 와야 한다.</li>
 *     <li>테스트용 가짜 객체도 세 메서드를 전부 구현해야 한다.</li>
 * </ul>
 */
public interface EmployeeOperations {

    /** 이번 주 세후 급여. */
    long calculatePay(String employeeId);

    /** 이번 주 근무시간. */
    long weeklyHours(String employeeId);

    /** 연락처. */
    Contact contactOf(String employeeId);
}

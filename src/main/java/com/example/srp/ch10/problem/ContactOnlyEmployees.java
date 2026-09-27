package com.example.srp.ch10.problem;

import com.example.srp.ch10.Contact;
import com.example.srp.ch10.EmployeeRoster;

/**
 * ⚠️ 세율표 서버 장애 중에도 공지 메일은 나가야 해서 급하게 만든 경량 구현.
 *
 * <p>{@code NoticeMailer} 에 필요한 건 {@code contactOf()} 하나뿐이다.
 * 그런데 {@link EmployeeOperations} 를 구현하려면 세 메서드를 전부 채워야 한다.
 * 그래서 나머지 둘은 예외로 막았다.
 *
 * <p>공지 메일은 다시 나간다. 대신 이 객체는 <b>{@code EmployeeOperations} 타입이면서
 * {@code EmployeeOperations} 의 약속을 지키지 않는다.</b> 9장의 LSP 위반이다.
 * {@code new PayrollBatch(new ContactOnlyEmployees(roster))} 는 컴파일러가 막지 않는다.
 * 터지는 건 운영 중이다.
 *
 * <p>뚱뚱한 인터페이스는 이렇게 구현하는 쪽을 LSP 위반으로 몰아간다.
 */
public class ContactOnlyEmployees implements EmployeeOperations {

    private final EmployeeRoster roster;

    public ContactOnlyEmployees(EmployeeRoster roster) {
        this.roster = roster;
    }

    @Override
    public long calculatePay(String employeeId) {
        throw new UnsupportedOperationException("연락처 전용 구현입니다. 급여는 계산할 수 없습니다.");
    }

    @Override
    public long weeklyHours(String employeeId) {
        throw new UnsupportedOperationException("연락처 전용 구현입니다. 근무시간은 알 수 없습니다.");
    }

    @Override
    public Contact contactOf(String employeeId) {
        return roster.contactOf(employeeId);
    }
}

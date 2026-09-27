package com.example.srp.ch10.problem;

import com.example.srp.ch10.Contact;
import com.example.srp.ch10.Employee;
import com.example.srp.ch10.EmployeeRoster;
import com.example.srp.ch10.TaxTable;
import com.example.srp.ch10.TaxTableLoader;

/**
 * {@link EmployeeOperations} 의 유일한 구현체.
 *
 * <p>생성할 때 세율표를 불러온다. 스프링 빈이 초기화 시점에 외부 자원을 읽어 오는 것과 같다.
 * 세율표 서버가 죽어 있으면 <b>이 객체는 만들어지지 않는다.</b>
 *
 * <p>세율표가 필요한 건 {@code calculatePay()} 하나뿐이다.
 * 하지만 {@code NoticeMailer} 가 연락처를 얻을 길이 이 객체뿐이므로,
 * 공지 메일까지 세율표 서버에 묶인다. 책의 그림 그대로다.
 * <pre>
 *  NoticeMailer ──▶ EmployeeService ──▶ 세율표 서버
 *  (System S)       (Framework F)        (Database D)
 * </pre>
 */
public class EmployeeService implements EmployeeOperations {

    private final EmployeeRoster roster;
    private final TaxTable taxTable;

    public EmployeeService(EmployeeRoster roster, TaxTableLoader taxTableLoader) {
        this.roster = roster;
        this.taxTable = taxTableLoader.load();   // ⚠️ 세율표 서버가 죽어 있으면 여기서 실패한다
    }

    @Override
    public long calculatePay(String employeeId) {
        Employee employee = roster.find(employeeId);
        return taxTable.afterTax(employee.hourlyRate() * employee.weeklyHours());
    }

    @Override
    public long weeklyHours(String employeeId) {
        return roster.weeklyHours(employeeId);
    }

    @Override
    public Contact contactOf(String employeeId) {
        return roster.contactOf(employeeId);
    }
}

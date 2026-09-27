package com.example.srp.ch10.solution;

import com.example.srp.ch10.Contact;
import com.example.srp.ch10.Employee;
import com.example.srp.ch10.EmployeeRoster;
import com.example.srp.ch10.TaxTable;
import com.example.srp.ch10.TaxTableLoader;

/**
 * 세 인터페이스를 모두 구현하는 하나의 구현체. 책의 그림에서 {@code OPS} 자리 그대로다.
 *
 * <p>쪼갠 건 <b>구현이 아니라 의존하는 창구</b>다. 평소에는 이 객체 하나를 세 사용자에게 모두 넘기면 된다.
 * 코드 내용은 problem 의 {@code EmployeeService} 와 똑같다. 달라진 건 {@code implements} 줄뿐이다.
 *
 * <p>그런데 창구가 쪼개졌기 때문에 <b>선택지</b>가 생겼다.
 * 세율표 서버가 죽어서 이 객체를 못 만들 때도, 공지 메일과 초과근무 감시에는
 * 세율표와 무관한 다른 구현(예: {@code roster::contactOf})을 넘길 수 있다.
 * problem 에서는 그러려면 쓰지도 않는 메서드를 예외로 채운 가짜 구현을 만들어야 했다.
 */
public class EmployeeService implements PayrollOperations, WorkHoursOperations, EmployeeDirectory {

    private final EmployeeRoster roster;
    private final TaxTable taxTable;

    public EmployeeService(EmployeeRoster roster, TaxTableLoader taxTableLoader) {
        this.roster = roster;
        this.taxTable = taxTableLoader.load();
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

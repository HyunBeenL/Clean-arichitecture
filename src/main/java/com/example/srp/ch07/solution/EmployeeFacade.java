package com.example.srp.ch07.solution;

import java.util.List;

import com.example.srp.ch07.TimeCard;

/**
 * 퍼사드(Facade).
 *
 * <p>책임을 셋으로 쪼개면 "쓰는 쪽이 클래스 세 개를 알아야 한다"는 불편이 생긴다.
 * 퍼사드는 그 불편만 해결한다. <b>업무 규칙은 한 줄도 갖지 않는다</b> —
 * 생성하고 위임할 뿐이다.
 *
 * <p>덕분에 호출하는 쪽 코드는 problem 패키지와 거의 똑같은 모양으로 유지된다.
 * <pre>{@code
 * EmployeeFacade employee = new EmployeeFacade("김개발", timeCards);
 * employee.calculatePay();
 * employee.reportHours();
 * employee.save();
 * }</pre>
 *
 * <p>겉모습은 같지만 속은 완전히 다르다.
 * 회계팀 규칙을 고쳐도 인사팀 보고서는 움직이지 않는다.
 */
public class EmployeeFacade {

    private final EmployeeData employeeData;

    private final PayCalculator payCalculator = new PayCalculator();
    private final HourReporter hourReporter = new HourReporter();
    private final EmployeeSaver employeeSaver = new EmployeeSaver();

    public EmployeeFacade(String name, List<TimeCard> timeCards) {
        this.employeeData = new EmployeeData(name, timeCards);
    }

    /** 회계팀(CFO) 기능으로 위임. */
    public long calculatePay() {
        return payCalculator.calculatePay(employeeData);
    }

    /** 인사팀(COO) 기능으로 위임. */
    public String reportHours() {
        return hourReporter.reportHours(employeeData);
    }

    /** DBA(CTO) 기능으로 위임. */
    public void save() {
        employeeSaver.save(employeeData);
    }

    public EmployeeData employeeData() {
        return employeeData;
    }
}

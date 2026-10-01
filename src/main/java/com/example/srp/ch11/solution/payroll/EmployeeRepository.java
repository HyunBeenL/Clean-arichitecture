package com.example.srp.ch11.solution.payroll;

import java.util.List;

import com.example.srp.ch11.Employee;

/**
 * 급여 지급에 필요한 사원 목록을 준다.
 *
 * <p>이 인터페이스는 {@code infra} 가 아니라 <b>{@code payroll} 패키지에 있다.</b>
 * 사용하는 쪽(고수준 업무 규칙)이 "나는 이런 게 필요하다"고 정하고,
 * 세부사항(MySQL 구현)이 거기에 맞춘다. 이것이 "역전"의 핵심이다.
 */
@FunctionalInterface
public interface EmployeeRepository {

    List<Employee> findAll();
}

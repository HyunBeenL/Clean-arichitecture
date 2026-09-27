package com.example.srp.ch10;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 사원 명부. 사원 정보를 id 로 찾아준다.
 *
 * <p>외부 시스템에 기대지 않는 가벼운 객체다. 세율표 서버가 죽어도 명부는 멀쩡하다.
 */
public final class EmployeeRoster {

    private final Map<String, Employee> byId;

    public EmployeeRoster(List<Employee> employees) {
        this.byId = employees.stream().collect(Collectors.toUnmodifiableMap(Employee::id, Function.identity()));
    }

    public Employee find(String employeeId) {
        Employee employee = byId.get(employeeId);
        if (employee == null) {
            throw new NoSuchElementException("명부에 없는 사원입니다: " + employeeId);
        }
        return employee;
    }

    public Contact contactOf(String employeeId) {
        Employee employee = find(employeeId);
        return new Contact(employee.name(), employee.email());
    }

    public long weeklyHours(String employeeId) {
        return find(employeeId).weeklyHours();
    }
}

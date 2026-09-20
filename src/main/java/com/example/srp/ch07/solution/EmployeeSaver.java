package com.example.srp.ch07.solution;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 액터: <b>CTO (DBA)</b> — 저장 방식은 이 부서만 결정한다.
 *
 * <p>스키마가 바뀌어도 {@link PayCalculator}, {@link HourReporter} 는 열어볼 필요가 없다.
 * 7장이 지적한 두 번째 증상(병합 충돌)이 사라지는 지점이기도 하다.
 *
 * <p>problem 패키지의 저장소는 {@code static} 필드였지만 여기서는 인스턴스 필드다.
 * 테스트끼리 상태를 공유하지 않는다.
 */
public class EmployeeSaver {

    /** DB 대신 쓰는 임시 저장소. */
    private final Map<String, EmployeeData> database = new HashMap<>();

    public void save(EmployeeData employee) {
        database.put(employee.name(), employee);
    }

    public Optional<EmployeeData> findByName(String name) {
        return Optional.ofNullable(database.get(name));
    }
}

package com.example.srp.ch11.solution.infra;

import java.util.List;

import com.example.srp.ch11.Employee;
import com.example.srp.ch11.solution.payroll.EmployeeRepository;

/**
 * MySQL 에서 사원 목록을 읽는다. <b>세부사항</b>이다.
 *
 * <p>업무 규칙이 정한 {@link EmployeeRepository} 에 맞춰 구현한다.
 * {@code import} 가 {@code payroll} 을 향한다. 세부사항이 업무 규칙에 의존한다.
 *
 * <p>실제라면 JDBC 로 운영 DB 에 접속한다. 이 예제 환경에는 운영 DB 가 없으므로 접속 시도가 실패한다.
 */
public class MySqlEmployeeRepository implements EmployeeRepository {

    private static final String URL = "jdbc:mysql://payroll-db.internal:3306/payroll";

    @Override
    public List<Employee> findAll() {
        throw new IllegalStateException("DB에 연결할 수 없습니다: " + URL);
    }
}

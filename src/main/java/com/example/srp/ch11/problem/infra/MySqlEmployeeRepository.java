package com.example.srp.ch11.problem.infra;

import java.util.List;

import com.example.srp.ch11.Employee;

/**
 * MySQL 에서 사원 목록을 읽는다. <b>세부사항</b>이다.
 *
 * <p>실제라면 JDBC 로 운영 DB 에 접속한다. 이 예제 환경에는 운영 DB 가 없으므로 접속 시도가 실패한다.
 * 테스트 환경에서 운영 DB 에 닿을 수 없는 건 현실에서도 마찬가지다.
 */
public class MySqlEmployeeRepository {

    private static final String URL = "jdbc:mysql://payroll-db.internal:3306/payroll";

    public List<Employee> findAll() {
        throw new IllegalStateException("DB에 연결할 수 없습니다: " + URL);
    }
}

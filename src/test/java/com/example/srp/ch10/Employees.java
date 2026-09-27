package com.example.srp.ch10;

import java.util.List;

/**
 * 테스트용 사원 명부와 세율표 픽스처. problem / solution 양쪽 테스트가 함께 쓴다.
 *
 * <table border="1">
 *     <caption>사원</caption>
 *     <tr><th>id</th><th>이름</th><th>시급</th><th>주 근무</th><th>세후 급여 (세율 10%)</th></tr>
 *     <tr><td>E1</td><td>김개발</td><td>20,000원</td><td>45시간</td><td>810,000원</td></tr>
 *     <tr><td>E2</td><td>이야근</td><td>18,000원</td><td>55시간</td><td>891,000원</td></tr>
 *     <tr><td>E3</td><td>박시간</td><td>15,000원</td><td>20시간</td><td>270,000원</td></tr>
 * </table>
 */
public final class Employees {

    public static final List<String> ALL_IDS = List.of("E1", "E2", "E3");

    /** 세율표 서버가 정상일 때. 세율 10%. */
    public static final TaxTableLoader TAX_SERVER_UP = () -> new TaxTable(10);

    /** 세율표 서버가 죽었을 때. */
    public static final TaxTableLoader TAX_SERVER_DOWN = () -> {
        throw new IllegalStateException("세율표 서버에 연결할 수 없습니다");
    };

    private Employees() {
    }

    public static EmployeeRoster roster() {
        return new EmployeeRoster(List.of(
                new Employee("E1", "김개발", "kim@example.com", 20_000, 45),
                new Employee("E2", "이야근", "lee@example.com", 18_000, 55),
                new Employee("E3", "박시간", "park@example.com", 15_000, 20)));
    }
}

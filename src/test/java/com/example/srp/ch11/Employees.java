package com.example.srp.ch11;

import java.util.List;

/**
 * 테스트용 사원 픽스처.
 *
 * <table border="1">
 *     <caption>사원</caption>
 *     <tr><th>id</th><th>이름</th><th>시급</th><th>주 근무</th><th>급여</th></tr>
 *     <tr><td>E1</td><td>김개발</td><td>20,000원</td><td>45시간</td><td>40×20,000 + 5×30,000 = 950,000원</td></tr>
 *     <tr><td>E2</td><td>박시간</td><td>15,000원</td><td>20시간</td><td>20×15,000 = 300,000원</td></tr>
 * </table>
 */
public final class Employees {

    public static final Employee KIM = new Employee("E1", "김개발", "kim@example.com", 20_000, 45);
    public static final Employee PARK = new Employee("E2", "박시간", "park@example.com", 15_000, 20);

    public static final List<Employee> ALL = List.of(KIM, PARK);

    private Employees() {
    }
}

package com.example.srp.ch10.solution;

import java.util.List;

import com.example.srp.ch10.EmployeeRoster;
import com.example.srp.ch10.Employees;
import com.example.srp.ch10.SourceCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ★ ch10 solution 의 핵심 — <b>쓰는 것에만 의존한다.</b>
 *
 * <ol>
 *     <li>구조적 증거: 사용자마다 의존하는 메서드 = 쓰는 메서드</li>
 *     <li>평소에는 구현체 하나가 세 인터페이스를 모두 구현한다 (책의 그림 그대로)</li>
 *     <li>세율표 장애 때는 급여 배치만 멈추고, 나머지는 세율표와 무관한 구현으로 돌아간다</li>
 *     <li>예외로 막아 둔 가짜 구현이 0곳이다</li>
 *     <li>회계팀 변경이 급여 쪽 파일에서 끝난다</li>
 * </ol>
 */
@DisplayName("ch10 solution · 쓰는 것에만 의존한다")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InterfaceSegregationTest {

    private static final List<Class<?>> USERS = List.of(PayrollBatch.class, OvertimeMonitor.class, NoticeMailer.class);

    private final EmployeeRoster roster = Employees.roster();

    @Test
    @Order(1)
    @DisplayName("1. 사용자마다 의존하는 메서드와 쓰는 메서드가 정확히 같다")
    void usersDependOnlyOnWhatTheyUse() {
        System.out.println();
        System.out.println("=================================================================");
        System.out.println(" 사용자별 의존하는 메서드 vs 실제로 쓰는 메서드");
        System.out.println("=================================================================");
        for (Class<?> user : USERS) {
            List<String> depended = SourceCode.dependedMethods(user);
            List<String> used = SourceCode.usedMethods(user);
            System.out.printf("  %-16s 의존 %d개 / 사용 %d개   %s%n",
                    user.getSimpleName(), depended.size(), used.size(), depended);

            assertThat(depended).isEqualTo(used).hasSize(1);
        }
        System.out.println("  -> problem 에서는 셋 다 '의존 3개 / 사용 1개'였다.");
        System.out.println("=================================================================");
        System.out.println();
    }

    @Test
    @Order(2)
    @DisplayName("2. 평소에는 EmployeeService 하나를 세 사용자에게 모두 넘긴다")
    void oneImplementationThreeInterfaces() {
        EmployeeService service = new EmployeeService(roster, Employees.TAX_SERVER_UP);

        assertThat(new PayrollBatch(service).totalPay(Employees.ALL_IDS)).isEqualTo(1_971_000);
        assertThat(new OvertimeMonitor(service).findOverworked(Employees.ALL_IDS)).containsExactly("E2");
        assertThat(new NoticeMailer(service).send(Employees.ALL_IDS, "공지")).hasSize(3);
    }

    @Test
    @Order(3)
    @DisplayName("3. 세율표 장애: 급여 배치만 멈추고 공지 메일과 초과근무 감시는 계속 돈다")
    void taxOutageStopsOnlyPayroll() {
        // 급여 계산은 정말로 세율표가 필요하다. 이건 멈추는 게 맞다.
        assertThatThrownBy(() -> new EmployeeService(roster, Employees.TAX_SERVER_DOWN))
                .isInstanceOf(IllegalStateException.class);

        // 나머지 둘은 세율표와 무관한 구현을 넘긴다. 메서드 참조 한 줄이면 된다.
        NoticeMailer mailer = new NoticeMailer(roster::contactOf);
        OvertimeMonitor monitor = new OvertimeMonitor(roster::weeklyHours);

        assertThat(mailer.send(Employees.ALL_IDS, "세율표 서버 점검 안내")).hasSize(3);
        assertThat(monitor.findOverworked(Employees.ALL_IDS)).containsExactly("E2");

        // new PayrollBatch(roster::contactOf) 는 컴파일되지 않는다.
        // problem 의 ContactOnlyEmployees 는 컴파일되고 운영 중에 터졌다.
    }

    @Test
    @Order(4)
    @DisplayName("4. 쓰지 않는 메서드를 예외로 막아 둔 구현이 0곳이다")
    void noUnsupportedOperationStubs() {
        List<String> problem = SourceCode.filesMentioning("problem", "UnsupportedOperationException",
                "IspViolationTest");
        List<String> solution = SourceCode.filesMentioning("solution", "UnsupportedOperationException",
                "InterfaceSegregationTest");

        System.out.println();
        System.out.println("=================================================================");
        System.out.println(" 쓰지도 않는 메서드를 UnsupportedOperationException 으로 채운 파일");
        System.out.println("=================================================================");
        System.out.printf("  problem  : %d개 %s%n", problem.size(), problem);
        System.out.printf("  solution : %d개%n", solution.size());
        System.out.println("  뚱뚱한 인터페이스는 구현하는 쪽을 LSP 위반으로 몰아간다 (9장).");
        System.out.println("=================================================================");
        System.out.println();

        assertThat(problem).isNotEmpty();
        assertThat(solution).isEmpty();
    }

    @Test
    @Order(5)
    @DisplayName("5. [변경 영향] 회계팀이 calculatePay 를 바꿔도 급여 쪽 파일만 고친다")
    void accountingChangeStaysInPayroll() {
        List<String> problem = SourceCode.filesMentioning("problem", "calculatePay", "IspViolationTest");
        List<String> solution = SourceCode.filesMentioning("solution", "calculatePay", "InterfaceSegregationTest");

        System.out.println();
        System.out.println("=================================================================");
        System.out.println(" 회계팀 요청: calculatePay 에 지급월(YearMonth) 파라미터 추가");
        System.out.println("=================================================================");
        System.out.printf("  problem  (%d개)%n", problem.size());
        problem.forEach(file -> System.out.println("    " + file));
        System.out.printf("  solution (%d개)%n", solution.size());
        solution.forEach(file -> System.out.println("    " + file));
        System.out.println("  ---------------------------------------------------------------");
        System.out.println("  solution 목록에 Mailer 도 Overtime 도 없다. 급여를 바꾸면 급여 쪽만 바뀐다.");
        System.out.println("=================================================================");
        System.out.println();

        assertThat(solution).noneMatch(file -> file.contains("Mailer") || file.contains("Overtime"));
        assertThat(problem).anyMatch(file -> file.contains("Mailer") || file.contains("Overtime"));
        assertThat(solution.size()).isLessThan(problem.size());
    }
}

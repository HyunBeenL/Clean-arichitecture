package com.example.srp.ch10.problem;

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
 * ★ ch10 problem 의 핵심 — <b>쓰지도 않는 것에 묶여 있다.</b>
 *
 * <ol>
 *     <li>구조적 증거: 사용자마다 의존하는 메서드와 실제로 쓰는 메서드를 타입 정보와 소스에서 세어 비교한다.</li>
 *     <li>아키텍처 사고: 세율표 서버가 죽으면 세율표가 필요 없는 공지 메일까지 멈춘다.</li>
 *     <li>땜질과 그 대가: 연락처 전용 구현을 만들면 메일은 나가지만, LSP 를 어기는 지뢰가 생긴다.</li>
 *     <li>변경 영향: 회계팀이 {@code calculatePay} 를 바꾸면 메일·근무시간 쪽 파일까지 고쳐야 한다.</li>
 * </ol>
 */
@DisplayName("ch10 problem · 쓰지도 않는 것에 묶여 있다")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IspViolationTest {

    private static final List<Class<?>> USERS = List.of(PayrollBatch.class, OvertimeMonitor.class, NoticeMailer.class);

    private final EmployeeRoster roster = Employees.roster();

    @Test
    @Order(1)
    @DisplayName("1. 사용자마다 메서드 3개에 의존하지만 실제로 쓰는 건 1개다")
    void usersDependOnMoreThanTheyUse() {
        System.out.println();
        System.out.println("=================================================================");
        System.out.println(" 사용자별 의존하는 메서드 vs 실제로 쓰는 메서드");
        System.out.println("=================================================================");
        for (Class<?> user : USERS) {
            List<String> depended = SourceCode.dependedMethods(user);
            List<String> used = SourceCode.usedMethods(user);
            List<String> unused = depended.stream().filter(name -> !used.contains(name)).toList();
            System.out.printf("  %-16s 의존 %d개 / 사용 %d개   안 쓰는데 묶인 것: %s%n",
                    user.getSimpleName(), depended.size(), used.size(), unused);

            assertThat(depended).hasSize(3);
            assertThat(used).hasSize(1);
        }
        System.out.println("=================================================================");
        System.out.println();
    }

    @Test
    @Order(2)
    @DisplayName("2. [사고] 세율표 서버가 죽으면 세율표가 필요 없는 공지 메일도 못 보낸다")
    void taxOutageStopsMailer() {
        // NoticeMailer 를 만들려면 EmployeeOperations 가 필요하고,
        // 그 구현체 EmployeeService 는 세율표가 있어야 만들어진다.
        assertThatThrownBy(() -> new NoticeMailer(new EmployeeService(roster, Employees.TAX_SERVER_DOWN)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("세율표");

        System.out.println();
        System.out.println("  NoticeMailer ──▶ EmployeeService ──▶ 세율표 서버 (장애)");
        System.out.println("  공지 메일은 세율표를 한 번도 쓰지 않는다. 그런데 같이 쓰러졌다.");
        System.out.println("  책의 S → F → D 그림 그대로다.");
        System.out.println();
    }

    @Test
    @Order(3)
    @DisplayName("3. [땜질] 연락처 전용 구현을 만들면 메일은 다시 나간다")
    void contactOnlyLetsMailerWork() {
        NoticeMailer mailer = new NoticeMailer(new ContactOnlyEmployees(roster));

        assertThat(mailer.send(Employees.ALL_IDS, "세율표 서버 점검 안내")).hasSize(3);
    }

    @Test
    @Order(4)
    @DisplayName("4. [땜질의 대가] 같은 객체를 급여 배치에 넣어도 컴파일된다 → 운영 중에 터진다 (LSP 위반)")
    void contactOnlyIsLandmine() {
        EmployeeOperations contactOnly = new ContactOnlyEmployees(roster);
        PayrollBatch batch = new PayrollBatch(contactOnly);   // 컴파일러는 막지 않는다

        assertThatThrownBy(() -> batch.totalPay(Employees.ALL_IDS))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @Order(5)
    @DisplayName("5. [변경 영향] 회계팀이 calculatePay 를 바꾸면 메일·근무시간 쪽 파일까지 고쳐야 한다")
    void accountingChangeRipplesEverywhere() {
        List<String> files = SourceCode.filesMentioning("problem", "calculatePay", "IspViolationTest");

        System.out.println();
        System.out.println("=================================================================");
        System.out.println(" 회계팀 요청: calculatePay 에 지급월(YearMonth) 파라미터 추가");
        System.out.println(" → 함께 고쳐야 하는 파일");
        System.out.println("=================================================================");
        files.forEach(file -> System.out.println("  " + file));
        System.out.println("  ---------------------------------------------------------------");
        System.out.println("  NoticeMailerTest, OvertimeMonitorTest 는 급여와 아무 상관이 없다.");
        System.out.println("  그래도 EmployeeOperations 를 구현한 가짜 객체가 있어서 컴파일 에러가 난다.");
        System.out.println("  이것이 책이 말하는 '불필요한 재컴파일·재배포'의 실물이다.");
        System.out.println("=================================================================");
        System.out.println();

        assertThat(files).contains(
                "test/problem/NoticeMailerTest.java",
                "test/problem/OvertimeMonitorTest.java",
                "main/problem/ContactOnlyEmployees.java");
    }
}

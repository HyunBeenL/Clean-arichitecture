package com.example.srp.ch11.problem;

import java.util.List;

import com.example.srp.ch11.SourceCode;
import com.example.srp.ch11.problem.payroll.PayDay;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ★ ch11 problem 의 핵심 — <b>업무 규칙이 세부사항에 의존한다.</b>
 *
 * <ol>
 *     <li>구조적 증거: 소스 의존성이 업무 규칙(payroll) → 세부사항(infra) 방향이다.</li>
 *     <li>업무 규칙이 변동성 큰 구체 클래스를 직접 {@code new} 한다.</li>
 *     <li>사고: 급여 규칙 하나 확인하려 해도 운영 DB 가 있어야 한다.</li>
 *     <li>변경 영향: 세부사항(알림 수단)이 바뀌면 업무 규칙 파일을 고쳐야 한다.</li>
 * </ol>
 */
@DisplayName("ch11 problem · 업무 규칙이 세부사항에 의존한다")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DipViolationTest {

    private static final String PAYROLL = "problem/payroll";
    private static final String INFRA = "problem/infra";

    @Test
    @Order(1)
    @DisplayName("1. 소스 의존성이 업무 규칙 → 세부사항 방향이다")
    void dependencyPointsToDetails() {
        List<String> payrollToInfra = SourceCode.importsFrom(PAYROLL, INFRA);
        List<String> infraToPayroll = SourceCode.importsFrom(INFRA, PAYROLL);

        System.out.println();
        System.out.println("=================================================================");
        System.out.println(" 소스 코드 의존성 방향");
        System.out.println("=================================================================");
        System.out.printf("  payroll ──▶ infra : %d개%n", payrollToInfra.size());
        payrollToInfra.forEach(line -> System.out.println("      " + line));
        System.out.printf("  infra ──▶ payroll : %d개%n", infraToPayroll.size());
        System.out.println();
        System.out.println("  고수준 정책(급여 규칙)이 저수준 세부사항(MySQL, SMTP, PDF)을 import 한다.");
        System.out.println("  세부사항이 흔들리면 업무 규칙도 같이 흔들린다.");
        System.out.println("=================================================================");
        System.out.println();

        assertThat(payrollToInfra).isNotEmpty();
        assertThat(infraToPayroll).isEmpty();
    }

    @Test
    @Order(2)
    @DisplayName("2. 업무 규칙이 변동성 큰 구체 클래스를 직접 new 한다")
    void businessCreatesConcretes() {
        List<String> constructorCalls = SourceCode.classNamesIn(INFRA).stream()
                .map(name -> "new " + name + "(")
                .toList();

        List<String> lines = SourceCode.codeMentioning(PAYROLL, constructorCalls);

        System.out.println();
        System.out.println("  [업무 규칙 안의 new 구체클래스(...)]");
        lines.forEach(line -> System.out.println("    " + line));
        System.out.println("    -> 인터페이스로 받아도 new 하는 순간 구체의 이름을 언급하게 된다.");
        System.out.println();

        assertThat(lines).hasSize(3);
    }

    @Test
    @Order(3)
    @DisplayName("3. [사고] 급여 규칙 하나 확인하려 해도 운영 DB 가 있어야 한다")
    void cannotRunWithoutDatabase() {
        PayDay payDay = new PayDay();

        assertThatThrownBy(payDay::run)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DB");

        System.out.println();
        System.out.println("  PayDay 의 핵심은 payFor() 의 급여 규칙이다.");
        System.out.println("  그런데 그 규칙을 실행하려면 MySQL 부터 붙어야 하고, 그다음엔 메일 서버가 필요하다.");
        System.out.println("  업무 규칙이 세부사항 없이는 존재할 수 없는 상태다.");
        System.out.println();
    }

    @Test
    @Order(4)
    @DisplayName("4. [변경 영향] 알림을 메일에서 사내 메신저로 바꾸면 업무 규칙 파일을 고쳐야 한다")
    void detailChangeTouchesBusinessRule() {
        List<String> lines = SourceCode.codeMentioning(PAYROLL, List.of("SmtpMailSender", "mailSender"));

        System.out.println();
        System.out.println("=================================================================");
        System.out.println(" 요청: 급여명세서를 메일 대신 사내 메신저로 보내 주세요");
        System.out.println(" → 고쳐야 하는 줄");
        System.out.println("=================================================================");
        lines.forEach(line -> System.out.println("  " + line));
        System.out.println("  ---------------------------------------------------------------");
        System.out.println("  전부 업무 규칙 파일 PayDay.java 안에 있다.");
        System.out.println("  급여 규칙은 한 글자도 안 바뀌었는데, 세부사항 때문에 업무 규칙을 열고 다시 테스트해야 한다.");
        System.out.println("  'PDF 대신 HTML 명세서로'도 마찬가지다.");
        System.out.println("=================================================================");
        System.out.println();

        assertThat(lines).isNotEmpty().allMatch(line -> line.startsWith("PayDay.java"));
    }
}

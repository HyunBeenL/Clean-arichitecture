package com.example.srp.ch11.solution;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.example.srp.ch11.Employee;
import com.example.srp.ch11.Employees;
import com.example.srp.ch11.SourceCode;
import com.example.srp.ch11.solution.main.PayrollMain;
import com.example.srp.ch11.solution.payroll.PayDay;
import com.example.srp.ch11.solution.payroll.Payslip;
import com.example.srp.ch11.solution.payroll.PayslipFactory;
import com.example.srp.ch11.solution.payroll.PayslipSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ★ ch11 solution 의 핵심 — <b>의존성이 역전됐다.</b>
 *
 * <ol>
 *     <li>소스 의존성 방향이 세부사항(infra) → 업무 규칙(payroll) 로 뒤집혔다.</li>
 *     <li>업무 규칙에 세부사항 클래스 이름이 0번 등장한다.</li>
 *     <li>제어 흐름과 소스 의존성이 반대 방향이다. 이것이 "역전"이다.</li>
 *     <li>업무 규칙을 DB·메일 서버 없이 실행한다.</li>
 *     <li>세부사항을 바꿔 끼워도 업무 규칙은 한 줄도 안 고친다.</li>
 *     <li>구체 이름을 아는 곳(DIP 위반)은 main 한 곳에 모였다.</li>
 * </ol>
 */
@DisplayName("ch11 solution · 의존성이 역전됐다")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DependencyInversionTest {

    private static final String PAYROLL = "solution/payroll";
    private static final String INFRA = "solution/infra";
    private static final String MAIN = "solution/main";

    @Test
    @Order(1)
    @DisplayName("1. 소스 의존성 방향이 세부사항 → 업무 규칙으로 뒤집혔다")
    void dependencyIsInverted() {
        int problemDown = SourceCode.importsFrom("problem/payroll", "problem/infra").size();
        int problemUp = SourceCode.importsFrom("problem/infra", "problem/payroll").size();
        List<String> solutionDown = SourceCode.importsFrom(PAYROLL, INFRA);
        List<String> solutionUp = SourceCode.importsFrom(INFRA, PAYROLL);

        System.out.println();
        System.out.println("=================================================================");
        System.out.println(" import 방향");
        System.out.println("=================================================================");
        System.out.printf("  problem  : payroll ──▶ infra %d개   infra ──▶ payroll %d개%n", problemDown, problemUp);
        System.out.printf("  solution : payroll ──▶ infra %d개   infra ──▶ payroll %d개%n",
                solutionDown.size(), solutionUp.size());
        System.out.println();
        solutionUp.forEach(line -> System.out.println("    " + line));
        System.out.println();
        System.out.println("  세부사항이 업무 규칙의 인터페이스를 import 해서 구현한다.");
        System.out.println("  이 경계선을 넘는 화살표가 전부 한 방향(업무 규칙 쪽)을 향한다.");
        System.out.println("  이것이 22장에서 '의존성 규칙'이 된다.");
        System.out.println("=================================================================");
        System.out.println();

        assertThat(solutionDown).isEmpty();
        assertThat(solutionUp).isNotEmpty();
        assertThat(problemDown).isPositive();
    }

    @Test
    @Order(2)
    @DisplayName("2. 업무 규칙에 세부사항 클래스 이름이 한 번도 등장하지 않는다")
    void businessNeverNamesDetails() {
        List<String> infraNames = SourceCode.classNamesIn(INFRA);

        assertThat(SourceCode.codeMentioning(PAYROLL, infraNames)).isEmpty();
    }

    @Test
    @Order(3)
    @DisplayName("3. 제어 흐름과 소스 의존성이 반대 방향이다 (역전)")
    void controlFlowOpposesSourceDependency() throws IllegalAccessException {
        PayDay payDay = PayrollMain.createPayDay();

        System.out.println();
        System.out.println("=================================================================");
        System.out.println(" PayDay 의 필드: 선언 타입(소스 의존) vs 실제 객체(제어 흐름)");
        System.out.println("=================================================================");
        for (Field field : PayDay.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            Class<?> declared = field.getType();
            Class<?> actual = field.get(payDay).getClass();

            System.out.printf("  %-10s %-28s ◁── %s%n", field.getName(), qualified(declared), qualified(actual));

            assertThat(declared.getPackageName()).endsWith(".solution.payroll");
            assertThat(actual.getPackageName()).endsWith(".solution.infra");
            assertThat(declared).isAssignableFrom(actual);
        }
        System.out.println();
        System.out.println("  제어 흐름 : 실행 중에는 PayDay 가 infra 의 코드를 호출한다   (payroll → infra)");
        System.out.println("  소스 의존 : infra 가 payroll 의 인터페이스를 구현한다       (infra → payroll)");
        System.out.println("  → 둘이 반대 방향이다. 이것이 '의존성 역전'이다.");
        System.out.println("  인터페이스는 사용하는 쪽(payroll) 패키지에 있다. 소유자가 고수준이다.");
        System.out.println("=================================================================");
        System.out.println();
    }

    @Test
    @Order(4)
    @DisplayName("4. 업무 규칙을 DB·메일 서버 없이 실행한다 (problem 은 DB 연결에서 실패했다)")
    void runsWithoutInfrastructure() {
        PayDay payDay = new PayDay(
                () -> Employees.ALL,
                (employee, pay) -> new TestPayslip(employee.name() + ".txt"),
                (employee, payslip) -> { });

        assertThat(payDay.run()).isEqualTo(950_000 + 300_000);
    }

    // ------------------------------------------------------------------
    // ★ 결정적 증거 — 세부사항을 갈아 끼워도 업무 규칙은 그대로다
    // ------------------------------------------------------------------

    private record TestPayslip(String fileName) implements Payslip {

        @Override
        public byte[] content() {
            return new byte[0];
        }
    }

    /** 새 세부사항 ①: 사내 메신저 알림. 이 클래스는 테스트 파일 안에 있다. */
    private static final class MessengerPayslipSender implements PayslipSender {

        private final List<String> messages = new ArrayList<>();

        @Override
        public void send(Employee employee, Payslip payslip) {
            messages.add("@" + employee.name() + " 이번 주 급여명세서: " + payslip.fileName());
        }
    }

    /** 새 세부사항 ②: HTML 명세서. 이 클래스도 테스트 파일 안에 있다. */
    private static final class HtmlPayslipFactory implements PayslipFactory {

        @Override
        public Payslip makePayslip(Employee employee, long pay) {
            String html = "<h1>" + employee.name() + "</h1><p>" + pay + "원</p>";
            return new Payslip() {
                @Override
                public String fileName() {
                    return "급여명세서-" + employee.name() + ".html";
                }

                @Override
                public byte[] content() {
                    return html.getBytes(StandardCharsets.UTF_8);
                }
            };
        }
    }

    @Test
    @Order(5)
    @DisplayName("5. [결정적] 메일→메신저, PDF→HTML 로 바꿔도 업무 규칙은 한 줄도 안 고친다")
    void swapDetailsWithoutTouchingBusiness() {
        MessengerPayslipSender messenger = new MessengerPayslipSender();
        PayDay payDay = new PayDay(() -> Employees.ALL, new HtmlPayslipFactory(), messenger);

        long total = payDay.run();

        System.out.println();
        System.out.println("  [메신저로 보낸 알림]");
        messenger.messages.forEach(message -> System.out.println("    " + message));
        System.out.println("    -> MessengerPayslipSender, HtmlPayslipFactory 는 테스트 파일 안에 있다.");
        System.out.println("       payroll 패키지는 이런 세부사항이 생겼다는 것조차 모른다.");
        System.out.println("       problem 에서는 같은 요청 때문에 PayDay.java 를 열어야 했다.");
        System.out.println();

        assertThat(total).isEqualTo(1_250_000);
        assertThat(messenger.messages).containsExactly(
                "@김개발 이번 주 급여명세서: 급여명세서-김개발.html",
                "@박시간 이번 주 급여명세서: 급여명세서-박시간.html");
    }

    @Test
    @Order(6)
    @DisplayName("6. 구체 이름을 아는 곳(DIP 위반)은 main 한 곳에 모였다")
    void violationsGatheredInMain() {
        List<String> infraNames = SourceCode.classNamesIn(INFRA);

        List<String> inPayroll = SourceCode.filesMentioning(PAYROLL, infraNames);
        List<String> inMain = SourceCode.filesMentioning(MAIN, infraNames);

        System.out.println();
        System.out.println("=================================================================");
        System.out.println(" infra 의 구체 클래스 이름을 언급하는 파일 (infra 자신 제외)");
        System.out.println("=================================================================");
        System.out.printf("  payroll : %s%n", inPayroll);
        System.out.printf("  main    : %s%n", inMain);
        System.out.println();
        System.out.println("  어딘가에서는 new MySqlEmployeeRepository() 를 해야 한다. 위반을 없앨 수는 없다.");
        System.out.println("  대신 main 한 곳에 모아 격리했다. 알림 수단을 바꾸는 결정도 여기서 한다.");
        System.out.println("=================================================================");
        System.out.println();

        assertThat(inPayroll).isEmpty();
        assertThat(inMain).containsExactly("PayrollMain.java");

        // 운영 조립은 운영 환경에서만 돈다. 업무 규칙 테스트는 이 조립을 거치지 않는다.
        assertThatThrownBy(() -> PayrollMain.createPayDay().run()).hasMessageContaining("DB");
    }

    private static String qualified(Class<?> type) {
        String packageName = type.getPackageName();
        return packageName.substring(packageName.lastIndexOf('.') + 1) + "." + type.getSimpleName();
    }
}

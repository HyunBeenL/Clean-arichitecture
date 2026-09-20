package com.example.srp.ch08.solution;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ★ ch08 solution 의 핵심 — <b>확장에는 열려 있고 변경에는 닫혀 있다</b>는 것의 증명.
 *
 * <ol>
 *     <li>구조적 증거: 계산기·출력기에 고용형태 분기가 <b>0곳</b>임을 소스에서 확인</li>
 *     <li>행위적 증거: problem 에서 깨졌던 시간제 계산이 명세대로 나온다</li>
 *     <li>결정적 증거: <b>완전히 새로운 고용형태를 이 테스트 안에서 만들어</b>
 *         main 소스를 한 줄도 고치지 않고 동작시킨다</li>
 * </ol>
 */
@DisplayName("ch08 solution · 확장에는 열려 있고 변경에는 닫혀 있다")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OpenForExtensionTest {

    private static final Path PROBLEM_DIR = Path.of("src/main/java/com/example/srp/ch08/problem");
    private static final Path SOLUTION_DIR = Path.of("src/main/java/com/example/srp/ch08/solution");

    private static final List<String> PROBLEM_TYPE_WORDS = List.of("FULL_TIME", "CONTRACT", "PART_TIME");
    private static final List<String> SOLUTION_TYPE_WORDS =
            List.of("FullTimePolicy", "ContractPolicy", "PartTimePolicy");

    /** 고용형태를 몰라야 하는 코어 클래스들. */
    private static final List<String> CORE_FILES = List.of("PayrollCalculator.java", "PayslipPrinter.java");

    private final PayrollCalculator payrollCalculator = new PayrollCalculator();
    private final PayslipPrinter payslipPrinter = new PayslipPrinter();

    @Test
    @Order(1)
    @DisplayName("1. 계산기와 출력기에 고용형태 분기가 0곳이다")
    void coreHasNoBranches() {
        long problemBranches = countMentions(PROBLEM_DIR, CORE_FILES, PROBLEM_TYPE_WORDS);
        long solutionBranches = countMentions(SOLUTION_DIR, CORE_FILES, SOLUTION_TYPE_WORDS);

        System.out.println();
        System.out.println("=================================================================");
        System.out.println(" PayrollCalculator + PayslipPrinter 안의 고용형태 분기 개수");
        System.out.println("=================================================================");
        System.out.printf("  problem  : %d곳   <- 고용형태가 늘면 여기를 전부 열어야 한다%n", problemBranches);
        System.out.printf("  solution : %d곳   <- 고용형태가 3종이든 30종이든 이 파일들은 그대로다%n", solutionBranches);
        System.out.println("=================================================================");
        System.out.println();

        assertThat(solutionBranches).isZero();
        assertThat(problemBranches).isGreaterThan(5);
    }

    @Test
    @Order(2)
    @DisplayName("2. 시간제 규칙은 PartTimePolicy 한 파일에 모두 모여 있다")
    void oneTypeLivesInOneFile() {
        List<String> ruleMethods = List.of("code", "payslipTitle", "regularHoursLimit", "hourlyRate", "insuranceRate");
        String source = readFile(SOLUTION_DIR.resolve("PartTimePolicy.java"));

        assertThat(ruleMethods).allSatisfy(method -> assertThat(source).contains(method + "()"));
    }

    @Test
    @Order(3)
    @DisplayName("3. problem 에서 깨졌던 시간제 계산이 명세대로 나온다")
    void partTimeFollowsSpecification() {
        Payslip payslip = payrollCalculator.calculate(new Worker("박시간", new PartTimePolicy(), 45));

        assertThat(payslip.grossPay()).isEqualTo(675_000);   // problem: 712,500원
        assertThat(payslip.deduction()).isZero();            // problem: 64,125원
        assertThat(payslip.netPay()).isEqualTo(675_000);     // problem: 648,375원
        assertThat(payslipPrinter.print(payslip)).contains("[시간제 급여명세서]");
    }

    // ------------------------------------------------------------------
    // ★ 결정적 증거 — main 소스를 한 줄도 고치지 않고 고용형태를 추가한다
    // ------------------------------------------------------------------

    /**
     * 완전히 새로운 고용형태 "인턴".
     *
     * <p>이 클래스는 <b>테스트 파일 안에</b> 있다.
     * 즉 {@code src/main} 아래 코드는 이런 고용형태가 생겼다는 사실조차 모른다.
     * 그런데도 계산기와 출력기는 아무 수정 없이 인턴 급여를 계산해낸다.
     *
     * <p>그리고 메서드를 하나라도 빠뜨리면 <b>컴파일이 되지 않는다.</b>
     * problem 의 if/else 체인은 빠뜨려도 조용히 통과했다는 걸 떠올려보자.
     */
    private static final class InternPolicy implements PayrollPolicy {

        @Override
        public String code() {
            return "INTERN";
        }

        @Override
        public String payslipTitle() {
            return "인턴 급여명세서";
        }

        @Override
        public long regularHoursLimit() {
            return 40;
        }

        @Override
        public long hourlyRate() {
            return 12_000;
        }

        @Override
        public long insuranceRate() {
            return 0;
        }
    }

    @Test
    @Order(4)
    @DisplayName("4. [결정적] main 소스를 한 줄도 고치지 않고 새 고용형태가 동작한다")
    void newTypeWithoutTouchingMain() {
        Payslip payslip = payrollCalculator.calculate(new Worker("최인턴", new InternPolicy(), 45));

        // 정규 40 × 12,000 + 연장 5 × 18,000 = 570,000원, 공제 없음
        assertThat(payslip.grossPay()).isEqualTo(570_000);
        assertThat(payslip.deduction()).isZero();
        assertThat(payslip.netPay()).isEqualTo(570_000);
        assertThat(payslipPrinter.print(payslip)).contains("[인턴 급여명세서]");
    }

    @Test
    @Order(5)
    @DisplayName("5. 조립 지점도 defaults() 를 고치지 않고 밖에서 조립할 수 있다")
    void registerWithoutTouchingDefaults() {
        PayrollPolicies policies = new PayrollPolicies(List.of(
                new FullTimePolicy(),
                new ContractPolicy(),
                new PartTimePolicy(),
                new InternPolicy()));

        assertThat(policies.registeredCodes()).contains("INTERN");
        assertThat(policies.byCode("INTERN").payslipTitle()).isEqualTo("인턴 급여명세서");
    }

    @Test
    @Order(6)
    @DisplayName("6. [비교표] 같은 요구사항을 problem 과 solution 이 어떻게 처리했는지")
    void printComparison() {
        System.out.println();
        System.out.println("=================================================================");
        System.out.println(" 고용형태를 하나 추가할 때 일어나는 일");
        System.out.println("=================================================================");
        printRow("", "problem", "solution");
        System.out.println("  " + "-".repeat(61));
        printRow("새로 만드는 파일", "0개", "1개");
        printRow("열어서 고치는 파일", "2개", "0개 (조립 지점 제외)");
        printRow("고쳐야 할 분기", "9곳", "0곳");
        printRow("빠뜨리면", "조용히 잘못 계산", "컴파일 에러");
        System.out.println();
        System.out.println("  problem : 고용형태별 규칙이 메서드 단위로 쪼개져 있다 (열 단위 조직).");
        System.out.println("            행이 하나 늘면 모든 열을 건드려야 한다.");
        System.out.println("  solution: 고용형태 하나가 파일 하나다 (행 단위 조직).");
        System.out.println("            행이 하나 늘면 파일 하나만 새로 만든다.");
        System.out.println();
        System.out.println("  위 4번 테스트의 InternPolicy 가 증거다. 이 클래스는 테스트 파일 안에 있고,");
        System.out.println("  src/main 아래 코드는 인턴이라는 고용형태가 생겼다는 것조차 모른다.");
        System.out.println("=================================================================");
        System.out.println();

        assertThat(countMentions(SOLUTION_DIR, CORE_FILES, SOLUTION_TYPE_WORDS)).isZero();
    }

    // ------------------------------------------------------------------
    // 소스 파일 스캔 도구
    // ------------------------------------------------------------------

    private static long countMentions(Path directory, List<String> fileNames, List<String> words) {
        try (Stream<Path> files = Files.list(directory)) {
            return files.filter(path -> fileNames.contains(path.getFileName().toString()))
                    .flatMap(OpenForExtensionTest::codeLines)
                    .filter(line -> words.stream().anyMatch(line::contains))
                    .count();
        } catch (IOException e) {
            throw new UncheckedIOException("소스를 읽지 못했습니다: " + directory.toAbsolutePath(), e);
        }
    }

    /** 주석을 제외한 코드 줄만 돌려준다. */
    private static Stream<String> codeLines(Path path) {
        return readFile(path).lines()
                .map(String::trim)
                .filter(line -> !line.startsWith("//") && !line.startsWith("*") && !line.startsWith("/*"));
    }

    private static String readFile(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("파일을 읽지 못했습니다: " + path, e);
        }
    }

    private static void printRow(String label, String problem, String solution) {
        System.out.println("  " + pad(label, 24) + pad(problem, 20) + solution);
    }

    /** 한글은 터미널에서 두 칸을 차지하므로 표시 너비 기준으로 공백을 채운다. */
    private static String pad(String text, int width) {
        int displayWidth = text.codePoints().map(OpenForExtensionTest::charWidth).sum();
        return text + " ".repeat(Math.max(width - displayWidth, 1));
    }

    private static int charWidth(int codePoint) {
        boolean wide = (codePoint >= 0x1100 && codePoint <= 0x115F)
                || (codePoint >= 0x2E80 && codePoint <= 0xA4CF)
                || (codePoint >= 0xAC00 && codePoint <= 0xD7A3)
                || (codePoint >= 0xF900 && codePoint <= 0xFAFF)
                || (codePoint >= 0xFF00 && codePoint <= 0xFF60);
        return wide ? 2 : 1;
    }
}

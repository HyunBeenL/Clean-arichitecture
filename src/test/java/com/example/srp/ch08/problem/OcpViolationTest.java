package com.example.srp.ch08.problem;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ★ ch08 problem 의 핵심 — OCP 위반의 증거.
 *
 * <p>두 가지를 보여준다.
 * <ol>
 *     <li>구조적 증거: 고용형태 분기가 몇 개 파일, 몇 군데에 흩어져 있는지
 *         소스 파일을 직접 읽어서 센다. (하드코딩이 아니다)</li>
 *     <li>행위적 증거: 그중 일부를 빠뜨린 결과 시간제 직원의 급여가
 *         실제로 어떻게 잘못 계산되는지 검증한다.</li>
 * </ol>
 */
@DisplayName("ch08 problem · OCP 위반 증거")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OcpViolationTest {

    private static final Path PROBLEM_DIR = Path.of("src/main/java/com/example/srp/ch08/problem");
    private static final List<String> CONSTANTS = List.of("FULL_TIME", "CONTRACT", "PART_TIME");

    private static final Worker PART_TIMER = new Worker("박시간", EmploymentType.PART_TIME, 45);

    private final PayrollCalculator payrollCalculator = new PayrollCalculator();
    private final PayslipPrinter payslipPrinter = new PayslipPrinter();

    @Test
    @Order(1)
    @DisplayName("1. 고용형태 분기가 여러 파일에 흩어져 있다 (산탄총 수술)")
    void branchesAreScattered() {
        List<BranchLine> branches = findBranchLines();

        System.out.println();
        System.out.println("=================================================================");
        System.out.println(" 고용형태를 하나 추가하려면 손대야 하는 곳");
        System.out.println("=================================================================");
        branches.forEach(line -> System.out.printf("  %-24s %s%n",
                line.fileName() + ":" + line.lineNumber(), line.text()));

        long fileCount = branches.stream().map(BranchLine::fileName).distinct().count();
        System.out.println("  ---------------------------------------------------------------");
        System.out.printf("  -> 파일 %d개, 분기 지점 %d곳%n", fileCount, branches.size());
        System.out.println("=================================================================");
        System.out.println();

        assertThat(fileCount).isGreaterThan(1);
        assertThat(branches).hasSizeGreaterThan(5);
    }

    @Test
    @Order(2)
    @DisplayName("2. 새 고용형태(PART_TIME)는 기존 것보다 적게 처리되어 있다 = 빠뜨린 곳이 있다")
    void newTypeIsHandledLessThanOthers() {
        Map<String, Long> counts = countByConstant();

        System.out.println();
        System.out.println("  [고용형태별로 코드에 등장하는 횟수]");
        counts.forEach((constant, count) -> System.out.printf("    %-12s %d곳%n", constant, count));
        System.out.println("    -> PART_TIME 만 유독 적다. 나머지 분기를 빠뜨렸다는 뜻이다.");
        System.out.println("       그런데도 컴파일은 통과했다.");
        System.out.println();

        assertThat(counts.get("PART_TIME")).isLessThan(counts.get("FULL_TIME"));
        assertThat(counts.get("PART_TIME")).isLessThan(counts.get("CONTRACT"));
    }

    @Test
    @Order(3)
    @DisplayName("3. [사고] 시간제인데 정규직과 같은 주 40시간 기준이 적용되어 연장수당이 붙는다")
    void partTimeGetsUnwantedOvertime() {
        Payslip payslip = payrollCalculator.calculate(PART_TIMER);

        // 회계팀 명세: 45 x 15,000 = 675,000원 (연장 개념 없음)
        // 실제:        정규 40 x 15,000 + 연장 5 x 22,500 = 712,500원
        assertThat(payslip.grossPay()).isEqualTo(712_500);
    }

    @Test
    @Order(4)
    @DisplayName("4. [사고] 공제 대상이 아닌데 4대보험 64,125원이 빠져나간다")
    void partTimeGetsUnwantedDeduction() {
        Payslip payslip = payrollCalculator.calculate(PART_TIMER);

        assertThat(payslip.deduction()).isEqualTo(64_125);   // 0원이어야 한다
        assertThat(payslip.netPay()).isEqualTo(648_375);     // 675,000원이어야 한다
    }

    @Test
    @Order(5)
    @DisplayName("5. [사고] 시간제 직원에게 정규직 급여명세서가 발송된다")
    void partTimeGetsWrongTitle() {
        String printed = payslipPrinter.print(payrollCalculator.calculate(PART_TIMER));

        assertThat(printed).contains("[정규직 급여명세서]");
        assertThat(printed).doesNotContain("[시간제 급여명세서]");
    }

    @Test
    @Order(6)
    @DisplayName("6. [비교표] 고용형태별 계산 결과를 명세와 나란히 출력한다")
    void printComparison() {
        Payslip fullTime = payrollCalculator.calculate(new Worker("김정규", EmploymentType.FULL_TIME, 45));
        Payslip contract = payrollCalculator.calculate(new Worker("이계약", EmploymentType.CONTRACT, 45));
        Payslip partTime = payrollCalculator.calculate(PART_TIMER);

        System.out.println();
        System.out.println("=================================================================");
        System.out.println(" 주 45시간 근무 · 고용형태별 계산 결과");
        System.out.println("=================================================================");
        printRow("고용형태", "지급총액", "공제액", "실수령액");
        System.out.println("  " + "-".repeat(61));
        printRow("정규직", won(fullTime.grossPay()), won(fullTime.deduction()), won(fullTime.netPay()));
        printRow("계약직", won(contract.grossPay()), won(contract.deduction()), won(contract.netPay()));
        printRow("시간제 (실제)", won(partTime.grossPay()), won(partTime.deduction()), won(partTime.netPay()));
        printRow("시간제 (명세)", won(675_000), won(0), won(675_000));
        System.out.println();
        System.out.println("  시간제만 명세와 다르다. 분기 4곳 중 1곳만 고쳤기 때문이다.");
        System.out.println("  급여는 실제 돈이 오가는 계산이다. 이런 누락은 곧바로 사고가 된다.");
        System.out.println();
        System.out.println("  [직접 해보기] PartTimeGapTest 의 @Disabled 를 지우고 실행해보자.");
        System.out.println("               회계팀 명세대로 쓴 테스트가 어떻게 깨지는지 볼 수 있다.");
        System.out.println("=================================================================");
        System.out.println();

        assertThat(partTime.netPay()).isNotEqualTo(675_000);
    }

    private record BranchLine(String fileName, int lineNumber, String text) {
    }

    /** problem 패키지 소스에서 고용형태 상수를 참조하는 줄을 모두 찾는다. */
    private static List<BranchLine> findBranchLines() {
        List<BranchLine> branches = new ArrayList<>();
        try (Stream<Path> files = Files.list(PROBLEM_DIR)) {
            files.filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> !path.getFileName().toString().equals("EmploymentType.java"))
                    .sorted()
                    .forEach(path -> collectFrom(path, branches));
        } catch (IOException e) {
            throw new UncheckedIOException("소스를 읽지 못했습니다: " + PROBLEM_DIR.toAbsolutePath(), e);
        }
        return branches;
    }

    private static void collectFrom(Path path, List<BranchLine> branches) {
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String trimmed = lines.get(i).trim();
                if (trimmed.startsWith("//") || trimmed.startsWith("*")) {
                    continue;
                }
                if (CONSTANTS.stream().anyMatch(trimmed::contains)) {
                    branches.add(new BranchLine(path.getFileName().toString(), i + 1, trimmed));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("파일을 읽지 못했습니다: " + path, e);
        }
    }

    private static Map<String, Long> countByConstant() {
        List<BranchLine> branches = findBranchLines();
        Map<String, Long> counts = new LinkedHashMap<>();
        for (String constant : CONSTANTS) {
            counts.put(constant, branches.stream().filter(line -> line.text().contains(constant)).count());
        }
        return counts;
    }

    private static String won(long amount) {
        return String.format("%,d원", amount);
    }

    private static void printRow(String a, String b, String c, String d) {
        System.out.println("  " + pad(a, 18) + pad(b, 15) + pad(c, 15) + d);
    }

    /** 한글은 터미널에서 두 칸을 차지하므로 표시 너비 기준으로 공백을 채운다. */
    private static String pad(String text, int width) {
        int displayWidth = text.codePoints().map(OcpViolationTest::charWidth).sum();
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

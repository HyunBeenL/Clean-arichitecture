package com.example.srp.ch07;

import java.util.List;

import com.example.srp.ch07.problem.EmployeeAfterCfoRequest;
import com.example.srp.ch07.solution.EmployeeData;
import com.example.srp.ch07.solution.HourReporter;
import com.example.srp.ch07.solution.PayCalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ★ 이 프로젝트의 결론 — <b>problem 과 solution 을 같은 입력으로 나란히 실행</b>한다.
 *
 * <p>양쪽 모두 회계팀(CFO)이 요청한 '주 40시간' 규칙을 반영한 상태다.
 * 그런데 인사팀(COO) 보고서의 운명이 갈린다.
 *
 * <ul>
 *     <li>problem : 회계팀 요청에 휩쓸려 정규 28시간 → 32시간으로 틀어짐</li>
 *     <li>solution: 인사팀 기준(하루 8시간)이 그대로 유지됨</li>
 * </ul>
 *
 * <p>아래 숫자는 하드코딩이 아니라 양쪽 구현을 실제로 호출해서 얻은 값이다.
 */
@DisplayName("problem vs solution 비교")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProblemVsSolutionTest {

    private static final List<TimeCard> WEEK = TimeCards.oneWeek();

    /** 문제 버전 — 회계팀 요청으로 regularHours() 를 고친 상태. */
    private final EmployeeAfterCfoRequest problem = new EmployeeAfterCfoRequest("김개발", WEEK);

    /** 해결 버전 — 액터별로 클래스를 나눈 상태. */
    private final EmployeeData employeeData = new EmployeeData("김개발", WEEK);
    private final PayCalculator payCalculator = new PayCalculator();
    private final HourReporter hourReporter = new HourReporter();

    @Test
    @Order(1)
    @DisplayName("1. 회계팀 요구는 양쪽 모두 똑같이 만족한다 (640,000원)")
    void bothSatisfyCfo() {
        assertThat(problem.calculatePay()).isEqualTo(640_000);
        assertThat(payCalculator.calculatePay(employeeData)).isEqualTo(640_000);
    }

    @Test
    @Order(2)
    @DisplayName("2. [결정적 차이] 인사팀 보고서는 한쪽에서만 살아남는다")
    void onlySolutionProtectsCoo() {
        // problem: 요청하지 않았는데 바뀌어 버렸다
        assertThat(problem.reportHours())
                .contains("정규 근무시간: 32시간")
                .contains("연장 근무시간: 0시간");

        // solution: 인사팀 기준 그대로다
        assertThat(hourReporter.reportHours(employeeData))
                .contains("정규 근무시간: 28시간")
                .contains("연장 근무시간: 4시간");

        assertThat(problem.reportHours()).isNotEqualTo(hourReporter.reportHours(employeeData));
    }

    @Test
    @Order(3)
    @DisplayName("3. [비교표] 두 버전의 결과를 나란히 출력한다")
    void printComparison() {
        long problemPay = problem.calculatePay();
        long solutionPay = payCalculator.calculatePay(employeeData);
        String problemReport = problem.reportHours();
        String solutionReport = hourReporter.reportHours(employeeData);

        System.out.println();
        System.out.println("=================================================================");
        System.out.println(" 근무 기록: 월 10h, 화 10h, 수 4h, 목 4h, 금 4h   (주 합계 32시간)");
        System.out.println(" 회계팀 요청: 정규 근무시간을 '하루 8시간' -> '주 40시간' 기준으로");
        System.out.println(" 양쪽 모두 이 요청을 반영한 상태다.");
        System.out.println("=================================================================");
        System.out.println();
        printRow("", "problem 패키지", "solution 패키지");
        System.out.println("  " + "-".repeat(61));
        printRow("급여 (회계팀 요청)", won(problemPay), won(solutionPay));
        printRow("보고서 정규시간 (인사팀)", hoursOf(problemReport, "정규"), hoursOf(solutionReport, "정규"));
        printRow("보고서 연장시간 (인사팀)", hoursOf(problemReport, "연장"), hoursOf(solutionReport, "연장"));
        System.out.println();
        System.out.println("  problem : calculatePay() 와 reportHours() 가 regularHours() 하나를 공유한다.");
        System.out.println("            회계팀 요청 한 건이 인사팀 보고서까지 끌고 갔다.");
        System.out.println("  solution: 회계팀 규칙은 PayCalculator 안에만, 인사팀 규칙은 HourReporter 안에만.");
        System.out.println("            두 클래스는 서로를 참조하지 않으므로 한쪽을 고쳐도 다른 쪽은 그대로다.");
        System.out.println();
        System.out.println("  [직접 해보기] PayCalculator 의 REGULAR_HOURS_PER_WEEK 를 35 로 바꾸고");
        System.out.println("               전체 테스트를 돌려보자. 깨지는 것은 PayCalculatorTest 뿐이다.");
        System.out.println("=================================================================");
        System.out.println();

        assertThat(problemReport).isNotEqualTo(solutionReport);
    }

    /** 보고서 문자열에서 "정규"/"연장" 근무시간 줄만 뽑아낸다. */
    private static String hoursOf(String report, String kind) {
        return report.lines()
                .filter(line -> line.startsWith(kind))
                .map(line -> line.substring(line.indexOf(':') + 1).trim())
                .findFirst()
                .orElse("-");
    }

    private static String won(long amount) {
        return String.format("%,d원", amount);
    }

    private static void printRow(String label, String problem, String solution) {
        System.out.println("  " + pad(label, 28) + pad(problem, 18) + solution);
    }

    /** 한글은 터미널에서 두 칸을 차지하므로 표시 너비 기준으로 공백을 채운다. */
    private static String pad(String text, int width) {
        int displayWidth = text.codePoints().map(ProblemVsSolutionTest::charWidth).sum();
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

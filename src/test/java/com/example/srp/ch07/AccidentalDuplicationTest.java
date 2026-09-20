package com.example.srp.ch07;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ★ 이 프로젝트의 핵심 — <b>우발적 중복(Accidental Duplication)</b> 실험.
 *
 * <h2>상황</h2>
 * <ol>
 *     <li>회계팀(CFO)이 급여 계산 방식을 바꿔달라고 요청한다.</li>
 *     <li>개발자는 {@code regularHours()} 한 곳만 고친다. → {@link EmployeeAfterCfoRequest}</li>
 *     <li>회계팀 요구는 정확히 반영되었다. 테스트도 통과한다.</li>
 *     <li><b>그런데 아무도 요청하지 않은 인사팀 보고서까지 바뀌어 있다.</b></li>
 * </ol>
 *
 * <p>같은 근무 기록을 변경 전/후 버전에 동시에 넣어 결과를 비교한다.
 */
@DisplayName("우발적 중복 실험")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccidentalDuplicationTest {

    private static final List<TimeCard> WEEK = TimeCards.oneWeek();

    private final Employee before = new Employee("김개발", WEEK);
    private final EmployeeAfterCfoRequest after = new EmployeeAfterCfoRequest("김개발", WEEK);

    @Test
    @Order(1)
    @DisplayName("1. [의도된 변경] 회계팀 요구대로 급여가 680,000원 → 640,000원으로 바뀐다")
    void payChangedAsRequested() {
        // 변경 전: 정규 28h × 20,000 + 연장 4h × 30,000 = 680,000원
        assertThat(before.calculatePay()).isEqualTo(680_000);

        // 변경 후: 주 40시간을 넘지 않았으므로 연장수당 없음 → 32h × 20,000 = 640,000원
        assertThat(after.calculatePay()).isEqualTo(640_000);

        // 여기까지는 아무 문제 없다. 회계팀이 요청한 그대로다.
    }

    @Test
    @Order(2)
    @DisplayName("2. [우발적 중복] 인사팀은 아무 요청도 하지 않았는데 보고서 수치가 함께 바뀐다")
    void hrReportChangedWithoutAnyRequest() {
        // 인사팀이 원래 보던 보고서
        assertThat(before.reportHours())
                .contains("정규 근무시간: 28시간")
                .contains("연장 근무시간: 4시간");

        // 인사팀은 그 어떤 변경도 요청한 적이 없다. 그런데 ...
        assertThat(after.reportHours())
                .contains("정규 근무시간: 32시간")   // 28시간 → 32시간
                .contains("연장 근무시간: 0시간");   //  4시간 →  0시간

        // 두 보고서는 더 이상 같지 않다.
        assertThat(after.reportHours()).isNotEqualTo(before.reportHours());
    }

    @Test
    @Order(3)
    @DisplayName("3. [연장근로 누락] 실제로 존재한 4시간의 연장근로가 보고서에서 사라진다")
    void overtimeDisappearsFromReport() {
        // 월 10시간, 화 10시간 — 하루 8시간을 넘긴 근무가 분명히 있었다.
        long hoursOverEightPerDay = WEEK.stream()
                .mapToLong(card -> Math.max(card.hours() - 8, 0))
                .sum();
        assertThat(hoursOverEightPerDay).isEqualTo(4);

        // 그런데 변경 후 보고서에는 연장근로가 0시간으로 찍힌다.
        // 인사팀이 이 보고서로 연장근로 한도를 관리하고 있었다면?
        assertThat(after.reportHours()).contains("연장 근무시간: 0시간");
    }

    @Test
    @Order(4)
    @DisplayName("4. [비교표] 변경 전/후를 콘솔에 나란히 출력한다")
    void printComparison() {
        long totalHours = WEEK.stream().mapToLong(TimeCard::hours).sum();

        System.out.println();
        System.out.println("=================================================================");
        System.out.println(" 근무 기록: 월 10h, 화 10h, 수 4h, 목 4h, 금 4h   (주 합계 " + totalHours + "시간)");
        System.out.println(" 변경 내용: regularHours() 를 '하루 8시간' → '주 40시간' 기준으로 수정");
        System.out.println("=================================================================");
        System.out.println();
        System.out.println("  [변경 전] -------------------------------------------");
        System.out.println("  회계팀(CFO) 급여     : " + String.format("%,d", before.calculatePay()) + "원");
        System.out.println(indent(before.reportHours()));
        System.out.println();
        System.out.println("  [변경 후] -------------------------------------------");
        System.out.println("  회계팀(CFO) 급여     : " + String.format("%,d", after.calculatePay()) + "원   <- 요청한 변경 (OK)");
        System.out.println(indent(after.reportHours()) + "   <- 요청한 적 없음 (!!)");
        System.out.println();
        System.out.println("  회계팀 요청 한 건으로 인사팀 보고서의 정규/연장 시간이 함께 움직였다.");
        System.out.println("  두 부서는 서로 다른 액터인데 같은 함수(regularHours)를 쓰고 있었기 때문이다.");
        System.out.println("=================================================================");
        System.out.println();
    }

    private static String indent(String text) {
        return text.lines().map(line -> "  " + line).reduce((a, b) -> a + System.lineSeparator() + b).orElse("");
    }
}

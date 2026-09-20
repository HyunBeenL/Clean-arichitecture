package com.example.srp.ch08.problem;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 회계팀이 요청한 <b>시간제(PART_TIME) 명세</b> 그대로 작성한 테스트.
 *
 * <ul>
 *     <li>정규 근무시간 제한 없음 — 45시간 전부 정규 근무</li>
 *     <li>시급 15,000원</li>
 *     <li>4대보험 공제 대상 아님</li>
 *     <li>명세서 제목은 "시간제 급여명세서"</li>
 * </ul>
 *
 * <p>즉 45 × 15,000 = <b>675,000원</b>이 그대로 실수령액이어야 한다.
 *
 * <p>아래 {@code @Disabled} 를 지우고 실행하면 <b>빨간불</b>이 뜬다.
 * 개발자는 {@code EmploymentType} 에 값을 추가하고 시급 분기까지는 고쳤지만,
 * 나머지 분기들이 다른 메서드·다른 파일에 흩어져 있어서 빠뜨렸기 때문이다.
 *
 * <p>그리고 <b>컴파일러는 아무것도 경고하지 않았다.</b>
 * enum 값을 추가했는데 처리하지 않은 분기가 있어도 {@code if/else} 체인은
 * 조용히 마지막 {@code return} 으로 떨어진다.
 */
@Disabled("""
        ★ 이 @Disabled 를 지우고 실행해보세요. 테스트가 깨지는 것이 정상입니다.
           회계팀 명세대로라면 통과해야 하는 테스트인데, 분기를 빠뜨려서 깨집니다.
        """)
@DisplayName("ch08 problem · 시간제 명세 (회계팀 요청)")
class PartTimeGapTest {

    private static final Worker PART_TIMER = new Worker("박시간", EmploymentType.PART_TIME, 45);

    private final Payslip payslip = new PayrollCalculator().calculate(PART_TIMER);

    @Test
    @DisplayName("정규 근무시간 제한이 없으므로 연장수당 없이 675,000원")
    void grossPay() {
        assertThat(payslip.grossPay()).isEqualTo(675_000);
    }

    @Test
    @DisplayName("4대보험 공제 대상이 아니므로 공제액은 0원")
    void noDeduction() {
        assertThat(payslip.deduction()).isZero();
        assertThat(payslip.netPay()).isEqualTo(675_000);
    }

    @Test
    @DisplayName("명세서 제목은 '시간제 급여명세서'")
    void title() {
        assertThat(new PayslipPrinter().print(payslip)).contains("[시간제 급여명세서]");
    }
}

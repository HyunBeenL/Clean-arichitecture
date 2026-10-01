package com.example.srp.ch11.problem;

import com.example.srp.ch11.problem.payroll.PayDay;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 급여팀이 쓰고 싶었던 단위 테스트.
 *
 * <blockquote>
 *     주 45시간, 시급 20,000원인 사원의 급여는 40 × 20,000 + 5 × 30,000 = <b>950,000원</b>이다.
 * </blockquote>
 *
 * <p>그런데 problem 의 {@link PayDay} 에는 이 사원 한 명을 넣을 방법이 없다.
 * {@code PayDay} 는 스스로 {@code new MySqlEmployeeRepository()} 를 해서 운영 DB 에서 사원을 읽고,
 * 급여 규칙 {@code payFor()} 는 private 이다. 할 수 있는 건 {@code run()} 을 부르는 것뿐이다.
 *
 * <p>아래 {@code @Disabled} 를 지우고 실행하면 <b>빨간불</b>이 뜬다.
 * 급여 규칙이 틀려서가 아니라, 규칙까지 도달하지도 못하고 DB 연결에서 실패한다.
 */
@Disabled("""
        ★ 이 @Disabled 를 지우고 실행해보세요. 테스트가 깨지는 것이 정상입니다.
           급여 규칙을 확인하고 싶었을 뿐인데 운영 DB 연결에서 실패합니다.
        """)
@DisplayName("ch11 problem · 급여 규칙 단위 테스트 (급여팀이 원하는 것)")
class PayRuleGapTest {

    @Test
    @DisplayName("주 45시간, 시급 20,000원 → 950,000원")
    void overtimePay() {
        PayDay payDay = new PayDay();   // 김개발 한 명만 넣고 싶지만 넣을 방법이 없다

        assertThat(payDay.run()).isEqualTo(950_000);
    }
}

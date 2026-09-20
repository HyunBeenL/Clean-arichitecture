package com.example.srp.ch08.solution;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 조립 지점 테스트.
 *
 * <p>OCP는 변경 지점을 0으로 만들어주지 않는다. <b>한 곳으로 모아줄 뿐이다.</b>
 * 그 한 곳이 여기이고, 빠뜨리면 조용히 잘못 계산되는 게 아니라 예외로 즉시 드러난다.
 */
@DisplayName("ch08 solution · PayrollPolicies (조립 지점)")
class PayrollPoliciesTest {

    @Test
    @DisplayName("기본 등록된 고용형태는 세 가지다")
    void defaults() {
        assertThat(PayrollPolicies.defaults().registeredCodes())
                .containsExactly("FULL_TIME", "CONTRACT", "PART_TIME");
    }

    @Test
    @DisplayName("코드로 정책을 찾을 수 있다")
    void findByCode() {
        PayrollPolicy policy = PayrollPolicies.defaults().byCode("PART_TIME");

        assertThat(policy).isInstanceOf(PartTimePolicy.class);
        assertThat(policy.payslipTitle()).isEqualTo("시간제 급여명세서");
    }

    @Test
    @DisplayName("등록되지 않은 고용형태는 조용히 넘어가지 않고 예외로 드러난다")
    void unregisteredCodeFailsFast() {
        assertThatThrownBy(() -> PayrollPolicies.defaults().byCode("INTERN"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("INTERN");
    }

    @Test
    @DisplayName("등록 목록은 밖에서 조립해 넣을 수도 있다 (defaults 를 고치지 않아도 된다)")
    void customRegistry() {
        PayrollPolicies policies = new PayrollPolicies(List.of(new FullTimePolicy(), new PartTimePolicy()));

        assertThat(policies.registeredCodes()).containsExactly("FULL_TIME", "PART_TIME");
    }
}

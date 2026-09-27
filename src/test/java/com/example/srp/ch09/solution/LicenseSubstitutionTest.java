package com.example.srp.ch09.solution;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import com.example.srp.ch09.SourceCode;
import com.example.srp.ch09.solution.license.Billing;
import com.example.srp.ch09.solution.license.BusinessLicense;
import com.example.srp.ch09.solution.license.License;
import com.example.srp.ch09.solution.license.PersonalLicense;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 책 9장의 License 예제 — <b>LSP 를 지킨 상속이 어떤 모습인지</b>의 기준점.
 */
@DisplayName("ch09 solution · License 는 서로 치환할 수 있다")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LicenseSubstitutionTest {

    private final Billing billing = new Billing();

    /** 이 테스트 안에서 새로 만든 라이선스. src/main 은 이 타입을 모른다. */
    private static final class StudentLicense implements License {

        @Override
        public long calcFee() {
            return 0;   // 학생은 무료. 0원도 계약("0원 이상") 안이다
        }
    }

    static Stream<License> allLicenses() {
        return Stream.of(new PersonalLicense(), new BusinessLicense(3), new StudentLicense());
    }

    @Test
    @Order(1)
    @DisplayName("1. 같은 Billing.charge() 가 넘겨받은 객체에 따라 다른 계산을 실행한다")
    void dynamicDispatch() {
        assertThat(billing.charge(new PersonalLicense())).isEqualTo(10_000);
        assertThat(billing.charge(new BusinessLicense(3))).isEqualTo(15_000);
    }

    @ParameterizedTest
    @MethodSource("allLicenses")
    @Order(2)
    @DisplayName("2. [계약 테스트] 모든 라이선스는 0원 이상의 사용료를 돌려준다")
    void everyLicenseKeepsContract(License license) {
        assertThat(license.calcFee()).isNotNegative();
    }

    @Test
    @Order(3)
    @DisplayName("3. Billing 은 하위 타입을 하나도 모른다")
    void billingKnowsNoSubtype() {
        List<String> lines = SourceCode.linesMentioning(Path.of("solution/license/Billing.java"),
                "PersonalLicense", "BusinessLicense", "instanceof");

        assertThat(lines).isEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("4. main 에 없는 새 라이선스도 Billing 수정 없이 청구된다")
    void newLicenseWithoutTouchingBilling() {
        long total = billing.chargeAll(allLicenses().toList());

        assertThat(total).isEqualTo(10_000 + 15_000 + 0);
    }
}

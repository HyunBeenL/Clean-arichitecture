package com.example.srp.ch09.solution;

import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import com.example.srp.ch09.SourceCode;
import com.example.srp.ch09.solution.taxi.DispatchFormats;
import com.example.srp.ch09.solution.taxi.DispatchUriBuilder;
import com.example.srp.ch09.solution.taxi.Ride;
import com.example.srp.ch09.solution.taxi.TaxiCompany;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ★ ch09 solution 의 핵심 (2) — 규약 위반을 <b>설정 데이터로 격리</b>한다.
 *
 * <p>problem 에서 도메인이 바뀌자 조용히 깨졌던 시나리오를
 * 여기서는 <b>코드를 한 줄도 고치지 않고</b> 설정만 바꿔서 해결한다.
 */
@DisplayName("ch09 solution · 규약 위반은 코드가 아니라 설정으로")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaxiDispatchTest {

    private static final Ride RIDE = new Ride("Bob", "24-Maple-St", "1530", "ORD");

    private static final TaxiCompany PURPLE = new TaxiCompany("퍼플택시", "https://purplecab.com");
    private static final TaxiCompany ACME = new TaxiCompany("애크미택시", "https://acme.com");

    private final DispatchUriBuilder builder = new DispatchUriBuilder(DispatchFormats.load());

    @Test
    @Order(1)
    @DisplayName("1. 규약을 지키는 회사는 설정이 없어도 표준 URI 가 나간다")
    void conformingCompany() {
        assertThat(builder.build(PURPLE, RIDE)).isEqualTo(
                "https://purplecab.com/driver/Bob/pickupAddress/24-Maple-St/pickupTime/1530/destination/ORD");
    }

    @Test
    @Order(2)
    @DisplayName("2. Acme 의 dest 는 설정 파일에서 읽어온다")
    void acmeFromConfigFile() {
        assertThat(builder.build(ACME, RIDE)).isEqualTo(
                "https://acme.com/driver/Bob/pickupAddress/24-Maple-St/pickupTime/1530/dest/ORD");
    }

    @Test
    @Order(3)
    @DisplayName("3. 배차 핵심 로직에 회사 이름이 0곳이다")
    void noCompanyNameInCoreLogic() {
        List<String> problem = SourceCode.linesMentioning(Path.of("problem/taxi/DispatchUriBuilder.java"), "acme");
        List<String> solution = SourceCode.linesMentioning(Path.of("solution/taxi/DispatchUriBuilder.java"),
                "acme", "purple");

        assertThat(problem).isNotEmpty();
        assertThat(solution).isEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("4. problem 에서 사고가 났던 도메인 변경이 설정 한 줄로 끝난다")
    void rebrandIsConfigChange() {
        // 설정 DB의 한 줄을 바꾼 것과 같다. DispatchUriBuilder 는 그대로다.
        Properties config = new Properties();
        config.setProperty("acmetaxi.co.kr.destination", "dest");
        DispatchUriBuilder rebuilt = new DispatchUriBuilder(DispatchFormats.fromProperties(config));

        String uri = rebuilt.build(new TaxiCompany("애크미택시", "https://acmetaxi.co.kr"), RIDE);

        assertThat(uri).endsWith("/dest/ORD");
    }

    @Test
    @Order(5)
    @DisplayName("5. 규약을 어기는 회사가 새로 생겨도 설정만 추가한다")
    void newNonConformingCompany() {
        Properties config = new Properties();
        config.setProperty("lazycab.kr.pickupAddress", "from");
        config.setProperty("lazycab.kr.destination", "to");
        DispatchUriBuilder rebuilt = new DispatchUriBuilder(DispatchFormats.fromProperties(config));

        String uri = rebuilt.build(new TaxiCompany("레이지캡", "https://lazycab.kr"), RIDE);

        System.out.println();
        System.out.println("=================================================================");
        System.out.println(" 규약 위반이 생겼을 때 고치는 곳");
        System.out.println("=================================================================");
        System.out.println("  problem  : DispatchUriBuilder 에 if 추가 → 재배포");
        System.out.println("  solution : dispatch-formats.properties 에 한 줄 추가");
        System.out.println();
        System.out.println("  " + uri);
        System.out.println();
        System.out.println("  예외가 사라진 게 아니라 격리된 것이다.");
        System.out.println("  규약 위반은 여전히 비용이지만, 더는 핵심 로직을 오염시키지 않는다.");
        System.out.println("=================================================================");
        System.out.println();

        assertThat(uri).isEqualTo("https://lazycab.kr/driver/Bob/from/24-Maple-St/pickupTime/1530/to/ORD");
    }
}

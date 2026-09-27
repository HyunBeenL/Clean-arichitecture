package com.example.srp.ch09.problem;

import java.nio.file.Path;
import java.util.List;

import com.example.srp.ch09.SourceCode;
import com.example.srp.ch09.problem.taxi.DispatchUriBuilder;
import com.example.srp.ch09.problem.taxi.Ride;
import com.example.srp.ch09.problem.taxi.TaxiCompany;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ★ ch09 problem 의 핵심 (2) — 아키텍처 수준의 LSP 위반.
 *
 * <p>인터페이스는 자바 interface 만이 아니다. 여러 서버가 함께 따르기로 한 <b>REST 규약</b>도 인터페이스다.
 * 구현체(택시 회사 서버) 하나가 규약을 어기면, 그 사정이 사용하는 쪽(배차 시스템)의 핵심 로직에 박힌다.
 */
@DisplayName("ch09 problem · 택시 배차 REST 규약 위반")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaxiDispatchViolationTest {

    private static final Ride RIDE = new Ride("Bob", "24-Maple-St", "1530", "ORD");

    private static final TaxiCompany PURPLE = new TaxiCompany("퍼플택시", "https://purplecab.com");
    private static final TaxiCompany ACME = new TaxiCompany("애크미택시", "https://acme.com");

    private final DispatchUriBuilder builder = new DispatchUriBuilder();

    /** Acme 서버를 흉내 낸다. 목적지를 {@code dest} 로만 받는다. */
    private static boolean acmeServerAccepts(String uri) {
        return uri.contains("/dest/") && !uri.contains("/destination/");
    }

    @Test
    @Order(1)
    @DisplayName("1. 규약을 지키는 회사에는 표준 URI 가 나간다")
    void conformingCompany() {
        assertThat(builder.build(PURPLE, RIDE)).isEqualTo(
                "https://purplecab.com/driver/Bob/pickupAddress/24-Maple-St/pickupTime/1530/destination/ORD");
    }

    @Test
    @Order(2)
    @DisplayName("2. 규약을 어긴 Acme 도 if 땜질 덕분에 오늘은 동작한다")
    void acmeWorksToday() {
        String uri = builder.build(ACME, RIDE);

        assertThat(uri).endsWith("/dest/ORD");
        assertThat(acmeServerAccepts(uri)).isTrue();
    }

    @Test
    @Order(3)
    @DisplayName("3. 대가: 배차 핵심 로직에 특정 회사의 도메인이 박혀 있다")
    void companyNameInCoreLogic() {
        List<String> lines = SourceCode.linesMentioning(Path.of("problem/taxi/DispatchUriBuilder.java"), "acme");

        System.out.println();
        System.out.println("=================================================================");
        System.out.println(" DispatchUriBuilder 안의 특정 회사 언급");
        System.out.println("=================================================================");
        lines.forEach(line -> System.out.println("  " + line));
        System.out.println("  -> 회사 하나의 사정 때문에 배차 시스템 전체를 고치고 다시 배포해야 한다.");
        System.out.println("=================================================================");
        System.out.println();

        assertThat(lines).isNotEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("4. [사고] Acme 가 도메인을 바꾸자 땜질이 조용히 빗나가고 배차가 실패한다")
    void acmeRebrandBreaksDispatch() {
        // DB의 dispatchBaseUri 만 바뀌었다. 코드는 아무도 건드리지 않았다.
        TaxiCompany rebrandedAcme = new TaxiCompany("애크미택시", "https://acmetaxi.co.kr");

        String uri = builder.build(rebrandedAcme, RIDE);

        System.out.println();
        System.out.println("  [Acme 도메인 변경 후 전송된 URI]");
        System.out.println("    " + uri);
        System.out.println("    -> Acme 서버는 destination 을 모른다. 배차 요청이 거부된다.");
        System.out.println("       코드의 if 는 여전히 \"acme.com\" 을 찾고 있다.");
        System.out.println();

        assertThat(uri).contains("/destination/");
        assertThat(acmeServerAccepts(uri)).isFalse();
    }
}

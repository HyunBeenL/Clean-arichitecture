package com.example.srp.ch09.problem.taxi;

/**
 * ⚠️ 아키텍처 수준의 LSP 위반을 떠안은 배차 URI 생성기.
 *
 * <p>모든 택시 회사는 같은 REST 규약으로 배차를 받기로 약속했다.
 * <pre>
 *  {baseUri}/driver/{기사}/pickupAddress/{승차지}/pickupTime/{시각}/destination/{목적지}
 * </pre>
 * 여기서 <b>REST 규약이 인터페이스</b>이고, <b>각 회사의 서버가 구현체</b>다.
 * 규약을 지키는 한 이 클래스는 어떤 회사로 보내는지 몰라도 된다.
 *
 * <p>그런데 Acme 만 {@code destination} 을 {@code dest} 로 줄여서 구현했다.
 * 구현체 하나가 인터페이스를 어긴 것이다. 그래서 핵심 로직에 <b>특정 회사의 도메인</b>이 박혔다.
 *
 * <p>이 땜질은 오늘은 동작한다. 하지만
 * <ul>
 *     <li>Acme 가 도메인을 바꾸거나 다른 회사에 합병되면 조용히 깨진다.</li>
 *     <li>규약을 어기는 회사가 또 생기면 이 파일에 {@code if} 가 하나씩 늘어난다.</li>
 *     <li>회사 하나의 사정 때문에 배차 시스템 전체를 다시 배포해야 한다.</li>
 * </ul>
 */
public class DispatchUriBuilder {

    public String build(TaxiCompany company, Ride ride) {
        String base = company.dispatchBaseUri();

        String destinationKey = "destination";
        // ⚠️ Acme 는 destination 을 dest 로 줄여서 구현했다.
        if (base.contains("acme.com")) {
            destinationKey = "dest";
        }

        return base
                + "/driver/" + ride.driver()
                + "/pickupAddress/" + ride.pickupAddress()
                + "/pickupTime/" + ride.pickupTime()
                + "/" + destinationKey + "/" + ride.destination();
    }
}

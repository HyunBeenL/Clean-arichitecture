package com.example.srp.ch09.solution.taxi;

/**
 * 배차 URI 생성기. <b>회사 이름을 하나도 모른다.</b>
 *
 * <p>problem 에서는 {@code if (base.contains("acme.com"))} 가 핵심 로직에 박혀 있었다.
 * 여기서는 URI 형식을 {@link DispatchFormats} 에 물어보기만 한다.
 * 규약을 어긴 회사가 늘어나든, 도메인을 바꾸든 이 파일은 그대로다.
 */
public class DispatchUriBuilder {

    private final DispatchFormats formats;

    public DispatchUriBuilder(DispatchFormats formats) {
        this.formats = formats;
    }

    public String build(TaxiCompany company, Ride ride) {
        String base = company.dispatchBaseUri();
        DispatchUriFormat format = formats.formatFor(base);

        return base
                + "/" + format.driver() + "/" + ride.driver()
                + "/" + format.pickupAddress() + "/" + ride.pickupAddress()
                + "/" + format.pickupTime() + "/" + ride.pickupTime()
                + "/" + format.destination() + "/" + ride.destination();
    }
}

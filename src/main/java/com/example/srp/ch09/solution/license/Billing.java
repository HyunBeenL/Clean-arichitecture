package com.example.srp.ch09.solution.license;

import java.util.List;

/**
 * 청구 시스템. {@link License} 만 안다.
 *
 * <p>이 파일에는 {@code PersonalLicense} 도, {@code BusinessLicense} 도, {@code instanceof} 도 없다.
 * {@code license.calcFee()} 가 어떤 구현을 실행할지는 실행 시점에 넘어온 객체가 정한다.
 *
 * <p>problem 의 {@code BannerLayoutPatched} 와 비교해보자.
 * 저쪽은 하위 타입이 계약을 어겨서 사용하는 쪽이 하위 타입을 알아야 했다.
 * 여기서는 모든 하위 타입이 계약을 지키므로 그럴 필요가 없다.
 */
public class Billing {

    public long charge(License license) {
        return license.calcFee();
    }

    public long chargeAll(List<? extends License> licenses) {
        return licenses.stream().mapToLong(this::charge).sum();
    }
}

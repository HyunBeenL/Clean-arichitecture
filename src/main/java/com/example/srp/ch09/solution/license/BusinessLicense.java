package com.example.srp.ch09.solution.license;

/**
 * 기업 라이선스. 사용자 1명당 월 5,000원.
 *
 * <p>{@link PersonalLicense} 와 계산 방식이 전혀 다르다. 그래도 {@link License} 의 계약
 * ("0원 이상의 사용료를 돌려준다")을 지키므로 서로 치환할 수 있다.
 */
public class BusinessLicense implements License {

    private static final long FEE_PER_USER = 5_000;

    private final int users;

    public BusinessLicense(int users) {
        if (users <= 0) {
            throw new IllegalArgumentException("사용자 수는 1명 이상이어야 합니다: " + users);
        }
        this.users = users;
    }

    @Override
    public long calcFee() {
        return users * FEE_PER_USER;
    }
}

package com.example.srp.ch09.solution.license;

/** 개인 라이선스. 월 10,000원 정액제. */
public class PersonalLicense implements License {

    private static final long MONTHLY_FEE = 10_000;

    @Override
    public long calcFee() {
        return MONTHLY_FEE;
    }
}

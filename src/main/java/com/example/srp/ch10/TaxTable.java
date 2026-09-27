package com.example.srp.ch10;

/** 세율표. 급여 계산에만 필요하다. */
public record TaxTable(long ratePercent) {

    public TaxTable {
        if (ratePercent < 0 || ratePercent > 100) {
            throw new IllegalArgumentException("세율은 0~100% 사이여야 합니다: " + ratePercent);
        }
    }

    public long afterTax(long grossPay) {
        return grossPay * (100 - ratePercent) / 100;
    }
}

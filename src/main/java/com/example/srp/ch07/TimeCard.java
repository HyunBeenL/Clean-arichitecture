package com.example.srp.ch07;

import java.time.LocalDate;

/** 하루치 근무 기록. */
public record TimeCard(LocalDate workDate, int hours) {

    public TimeCard {
        if (hours < 0) {
            throw new IllegalArgumentException("근무시간은 0 이상이어야 합니다.");
        }
    }
}

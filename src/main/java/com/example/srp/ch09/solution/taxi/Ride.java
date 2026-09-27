package com.example.srp.ch09.solution.taxi;

/** 배차 요청 한 건. 어느 택시 회사로 보내든 내용은 같다. */
public record Ride(String driver, String pickupAddress, String pickupTime, String destination) {
}

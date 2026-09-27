package com.example.srp.ch10;

/**
 * 사원 한 명의 기본 정보. problem / solution 양쪽이 함께 쓴다.
 *
 * <p>"이 사람은 시급이 얼마고 이번 주에 몇 시간 일했다"는 <b>사실</b>일 뿐,
 * 어느 사용자의 규칙도 담고 있지 않다. 그래서 공유해도 안전하다.
 */
public record Employee(String id, String name, String email, long hourlyRate, long weeklyHours) {
}

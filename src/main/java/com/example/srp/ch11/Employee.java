package com.example.srp.ch11;

/**
 * 사원 한 명의 기본 정보. problem / solution 양쪽이 함께 쓴다.
 *
 * <p>구체 클래스지만 의존해도 괜찮다. {@code String} 처럼 <b>거의 바뀌지 않는</b> 사실 데이터이기 때문이다.
 * DIP 가 피하라는 건 구체 전부가 아니라 <b>변동성이 큰</b> 구체다.
 */
public record Employee(String id, String name, String email, long hourlyRate, long weeklyHours) {
}

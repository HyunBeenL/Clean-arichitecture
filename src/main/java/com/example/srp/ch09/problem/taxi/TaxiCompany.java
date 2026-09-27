package com.example.srp.ch09.problem.taxi;

/**
 * 배차 서비스에 연결된 택시 회사.
 *
 * <p>{@code dispatchBaseUri} 는 보통 DB에 저장된 값이다.
 * 회사가 도메인을 바꾸면 이 값만 바뀐다.
 */
public record TaxiCompany(String name, String dispatchBaseUri) {
}

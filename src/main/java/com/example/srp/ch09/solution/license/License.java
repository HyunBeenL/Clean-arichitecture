package com.example.srp.ch09.solution.license;

/**
 * 소프트웨어 사용권(라이선스). 책 9장의 <b>LSP를 지키는 기준점</b> 예제다.
 *
 * <h2>계약</h2>
 * <ul>
 *     <li>{@link #calcFee()} 는 이번 청구 기간의 사용료를 원 단위로 돌려준다.</li>
 *     <li>사용료는 0원 이상이다. 음수를 돌려주거나 예외를 던지면 안 된다.</li>
 * </ul>
 *
 * <p>구현체가 이 계약만 지키면 {@link Billing} 은 어떤 라이선스가 들어오는지 몰라도 된다.
 * 계산 방식이 서로 완전히 달라도 괜찮다. LSP가 요구하는 건 <b>같은 계산</b>이 아니라
 * <b>같은 약속</b>이다.
 */
public interface License {

    long calcFee();
}

package com.example.srp.ch09.solution.shape;

/**
 * 넓이를 가진 도형.
 *
 * <p>problem 에서는 "정사각형은 직사각형이다"라는 <b>현실 세계의 IS-A 관계</b>를 그대로 상속으로 옮겼다가
 * 계약이 깨졌다. 여기서는 기준을 바꾼다.
 * 하위 타입 관계는 <b>행위(계약)가 같은가</b>로 정한다.
 *
 * <ul>
 *     <li>직사각형과 정사각형은 "넓이를 알려준다"는 행위를 공유한다 → 둘 다 {@code Shape}</li>
 *     <li>"너비만 따로 바꿀 수 있다"는 행위는 공유하지 않는다 → 서로 상속하지 않는다</li>
 * </ul>
 *
 * <h2>계약</h2>
 * {@link #area()} 는 0보다 큰 넓이를 돌려준다.
 */
public interface Shape {

    long area();
}

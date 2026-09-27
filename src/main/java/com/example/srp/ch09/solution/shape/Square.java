package com.example.srp.ch09.solution.shape;

/**
 * 불변 정사각형. {@link Rectangle} 을 상속하지 <b>않는다.</b>
 *
 * <p>정사각형은 너비와 높이를 따로 바꿀 수 없다. 즉 Rectangle 의 계약을 지킬 수 없다.
 * 그렇다면 Rectangle 의 하위 타입이 되어서는 안 된다.
 * 대신 둘이 진짜로 공유하는 계약인 {@link Shape} 만 구현한다.
 *
 * <p>그 결과 {@code Rectangle} 을 받는 메서드에 {@code Square} 를 넘기면 <b>컴파일 에러</b>가 난다.
 * problem 에서는 런타임에 조용히 틀렸던 실수가 컴파일 시점으로 당겨진다.
 */
public record Square(long side) implements Shape {

    public Square {
        if (side <= 0) {
            throw new IllegalArgumentException("한 변의 길이는 0보다 커야 합니다: " + side);
        }
    }

    public Square withSide(long newSide) {
        return new Square(newSide);
    }

    @Override
    public long area() {
        return side * side;
    }
}

package com.example.srp.ch09.problem.shape;

/**
 * ⚠️ LSP(리스코프 치환 원칙)를 위반한 정사각형.
 *
 * <p>"정사각형은 직사각형이다"라는 수학적 사실을 그대로 상속으로 옮겼다.
 * 정사각형은 너비와 높이가 항상 같아야 하므로 setter 를 재정의해 둘을 함께 바꾼다.
 *
 * <p>정사각형으로서는 올바른 동작이다. 문제는 {@link Rectangle} 의 계약
 * ("너비를 바꿔도 높이는 그대로다")을 깨뜨린다는 것이다.
 *
 * <pre>
 *  Rectangle r = new Square(1);
 *  r.setWidth(5);
 *  r.setHeight(2);
 *  r.area();          // Rectangle 을 믿은 사람의 기대: 10, 실제: 4
 * </pre>
 *
 * <p>컴파일러는 아무 말도 하지 않는다. {@code Rectangle r = new Square(90);} 은 완벽히 합법이다.
 * 깨지는 건 Rectangle 을 믿고 짠 <b>사용하는 쪽 코드</b>다.
 */
public class Square extends Rectangle {

    public Square(long side) {
        super(side, side);
    }

    @Override
    public void setWidth(long width) {
        super.setWidth(width);
        super.setHeight(width);   // ⚠️ 높이까지 몰래 바꾼다
    }

    @Override
    public void setHeight(long height) {
        super.setWidth(height);   // ⚠️ 너비까지 몰래 바꾼다
        super.setHeight(height);
    }
}

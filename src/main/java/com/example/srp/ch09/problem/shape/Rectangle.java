package com.example.srp.ch09.problem.shape;

/**
 * 가변 직사각형. 너비와 높이를 <b>각각 독립적으로</b> 바꿀 수 있다.
 *
 * <p>이 클래스를 쓰는 코드는 암묵적으로 다음 약속(계약)을 믿는다.
 * <pre>
 *  setWidth(w)  를 호출해도 height 는 그대로다.
 *  setHeight(h) 를 호출해도 width 는 그대로다.
 *  → 따라서 setWidth(5); setHeight(2); 뒤에는 area() == 10 이다.
 * </pre>
 *
 * <p>이 약속은 시그니처 어디에도 드러나지 않고, 컴파일러가 강제하지도 않는다.
 * 그런데도 Rectangle 을 받는 코드는 전부 이 약속에 기대어 작성된다.
 */
public class Rectangle {

    private long width;
    private long height;

    public Rectangle(long width, long height) {
        this.width = width;
        this.height = height;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public long getWidth() {
        return width;
    }

    public long getHeight() {
        return height;
    }

    public long area() {
        return width * height;
    }
}

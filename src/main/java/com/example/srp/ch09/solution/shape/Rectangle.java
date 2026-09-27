package com.example.srp.ch09.solution.shape;

/**
 * 불변 직사각형.
 *
 * <p>크기를 바꾸면 <b>새 객체</b>를 돌려준다. setter 가 없으니
 * "너비를 바꿨더니 높이도 바뀌었다" 같은 일은 원천적으로 일어날 수 없다.
 */
public record Rectangle(long width, long height) implements Shape {

    public Rectangle {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("너비와 높이는 0보다 커야 합니다: " + width + "x" + height);
        }
    }

    public Rectangle withWidth(long newWidth) {
        return new Rectangle(newWidth, height);
    }

    public Rectangle withHeight(long newHeight) {
        return new Rectangle(width, newHeight);
    }

    @Override
    public long area() {
        return width * height;
    }
}

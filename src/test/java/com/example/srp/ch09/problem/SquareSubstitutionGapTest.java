package com.example.srp.ch09.problem;

import com.example.srp.ch09.problem.shape.Rectangle;
import com.example.srp.ch09.problem.shape.Square;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Rectangle} 의 계약을 그대로 적은 테스트를 <b>Square 에 돌린다.</b>
 *
 * <ul>
 *     <li>너비를 바꿔도 높이는 그대로다.</li>
 *     <li>높이를 바꿔도 너비는 그대로다.</li>
 *     <li>그러므로 너비 5, 높이 2 로 맞추면 넓이는 10 이다.</li>
 * </ul>
 *
 * <p>LSP 를 테스트로 옮기면 이렇게 된다.
 * <b>상위 타입의 테스트는 모든 하위 타입에 대해서도 통과해야 한다.</b>
 *
 * <p>아래 {@code @Disabled} 를 지우고 실행하면 <b>빨간불</b>이 뜬다.
 * Square 가 Rectangle 의 하위 타입이 될 자격이 없다는 뜻이다.
 */
@Disabled("""
        ★ 이 @Disabled 를 지우고 실행해보세요. 테스트가 깨지는 것이 정상입니다.
           Rectangle 의 계약을 Square 에 그대로 적용하면 깨집니다. 이것이 LSP 위반입니다.
        """)
@DisplayName("ch09 problem · Rectangle 의 계약을 Square 에 적용한다")
class SquareSubstitutionGapTest {

    private final Rectangle rectangle = new Square(10);

    @Test
    @DisplayName("너비를 바꿔도 높이는 그대로다")
    void widthDoesNotChangeHeight() {
        rectangle.setWidth(5);

        assertThat(rectangle.getHeight()).isEqualTo(10);
    }

    @Test
    @DisplayName("높이를 바꿔도 너비는 그대로다")
    void heightDoesNotChangeWidth() {
        rectangle.setHeight(2);

        assertThat(rectangle.getWidth()).isEqualTo(10);
    }

    @Test
    @DisplayName("너비 5, 높이 2 로 맞추면 넓이는 10 이다")
    void areaIsWidthTimesHeight() {
        rectangle.setWidth(5);
        rectangle.setHeight(2);

        assertThat(rectangle.area()).isEqualTo(10);
    }
}

package com.example.srp.ch09.solution;

import java.nio.file.Path;
import java.util.stream.Stream;

import com.example.srp.ch09.SourceCode;
import com.example.srp.ch09.solution.shape.BannerLayout;
import com.example.srp.ch09.solution.shape.Rectangle;
import com.example.srp.ch09.solution.shape.Shape;
import com.example.srp.ch09.solution.shape.Square;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ★ ch09 solution 의 핵심 (1) — 하위 타입 관계를 <b>행위(계약)</b>로 정한다.
 *
 * <ul>
 *     <li>Square 는 Rectangle 의 계약을 지킬 수 없으므로 Rectangle 을 상속하지 않는다.</li>
 *     <li>둘이 진짜로 공유하는 계약(넓이)만 {@link Shape} 로 묶는다.</li>
 *     <li>그러면 사용하는 쪽에 {@code instanceof} 가 필요 없다.</li>
 * </ul>
 */
@DisplayName("ch09 solution · 계약을 공유하는 것끼리만 치환한다")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ShapeSubstitutionTest {

    private final BannerLayout layout = new BannerLayout();

    /** 이 테스트 안에서 새로 만든 도형. src/main 은 이 타입을 모른다. */
    private record RightTriangle(long base, long height) implements Shape {

        @Override
        public long area() {
            return base * height / 2;
        }
    }

    static Stream<Shape> allShapes() {
        return Stream.of(new Rectangle(300, 90), new Square(90), new RightTriangle(10, 20));
    }

    @Test
    @Order(1)
    @DisplayName("1. 너비를 늘리면 높이는 그대로인 새 Rectangle 이 나온다 (원본은 불변)")
    void stretchKeepsHeight() {
        Rectangle original = new Rectangle(300, 90);

        Rectangle stretched = layout.stretchToBannerWidth(original);

        assertThat(stretched).isEqualTo(new Rectangle(728, 90));
        assertThat(original).isEqualTo(new Rectangle(300, 90));
    }

    @Test
    @Order(2)
    @DisplayName("2. Square 는 Rectangle 이 아니다 → Rectangle 자리에 넣으면 컴파일 에러")
    void squareIsNotRectangle() {
        // layout.stretchToBannerWidth(new Square(90));   ← 주석을 풀면 컴파일되지 않는다
        // problem 에서는 이 줄이 컴파일되고 런타임에 조용히 틀렸다.
        assertThat(Rectangle.class.isAssignableFrom(Square.class)).isFalse();
        assertThat(Shape.class.isAssignableFrom(Square.class)).isTrue();
    }

    @ParameterizedTest
    @MethodSource("allShapes")
    @Order(3)
    @DisplayName("3. [계약 테스트] 모든 Shape 는 0보다 큰 넓이를 돌려준다")
    void everyShapeKeepsContract(Shape shape) {
        assertThat(shape.area()).isPositive();
    }

    @Test
    @Order(4)
    @DisplayName("4. 넓이만 필요한 곳에는 main 에 없는 새 도형까지 그대로 치환된다")
    void totalAreaAcceptsAnyShape() {
        long total = layout.totalArea(allShapes().toList());

        // 300x90 + 90x90 + 10x20/2
        assertThat(total).isEqualTo(27_000 + 8_100 + 100);
    }

    @Test
    @Order(5)
    @DisplayName("5. solution 의 도형 코드에는 instanceof 가 0곳이다")
    void noInstanceofInSolution() {
        long problem = SourceCode.countInDirectory(Path.of("problem/shape"), "instanceof");
        long solution = SourceCode.countInDirectory(Path.of("solution/shape"), "instanceof");

        System.out.println();
        System.out.println("=================================================================");
        System.out.println(" 하위 타입을 구분하는 instanceof 개수");
        System.out.println("=================================================================");
        System.out.printf("  problem  : %d곳   <- 계약을 어긴 하위 타입을 사용하는 쪽이 걸러낸다%n", problem);
        System.out.printf("  solution : %d곳   <- 걸러내는 일은 타입 시스템이 한다%n", solution);
        System.out.println("=================================================================");
        System.out.println();

        assertThat(solution).isZero();
        assertThat(problem).isPositive();
    }
}

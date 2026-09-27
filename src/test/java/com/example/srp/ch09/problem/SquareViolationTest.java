package com.example.srp.ch09.problem;

import java.nio.file.Path;
import java.util.List;

import com.example.srp.ch09.SourceCode;
import com.example.srp.ch09.problem.shape.BannerLayout;
import com.example.srp.ch09.problem.shape.BannerLayoutPatched;
import com.example.srp.ch09.problem.shape.Rectangle;
import com.example.srp.ch09.problem.shape.Square;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ★ ch09 problem 의 핵심 (1) — 정사각형/직사각형 문제.
 *
 * <ol>
 *     <li>Rectangle 로 쓰면 계약대로 동작한다.</li>
 *     <li>Square 로 치환하면 Rectangle 을 믿고 짠 코드가 틀린 결과를 낸다.</li>
 *     <li>{@code instanceof} 로 땜질하면 버그는 막히지만, 사용하는 쪽이 하위 타입을 알게 되고
 *         계약을 어기는 하위 타입이 또 생기면 다시 깨진다.</li>
 * </ol>
 */
@DisplayName("ch09 problem · Square 는 Rectangle 을 대신할 수 없다")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SquareViolationTest {

    private final BannerLayout layout = new BannerLayout();
    private final BannerLayoutPatched patchedLayout = new BannerLayoutPatched();

    @Test
    @Order(1)
    @DisplayName("1. Rectangle 을 넣으면 너비만 728px 로 늘어나고 높이는 그대로다")
    void rectangleKeepsContract() {
        Rectangle stretched = layout.stretchToBannerWidth(new Rectangle(300, 90));

        assertThat(stretched.getWidth()).isEqualTo(728);
        assertThat(stretched.getHeight()).isEqualTo(90);
        assertThat(stretched.area()).isEqualTo(65_520);
    }

    @Test
    @Order(2)
    @DisplayName("2. [사고] Square 를 넣으면 높이까지 728px 로 늘어난다")
    void squareBreaksCaller() {
        Rectangle adSlot = new Square(90);   // 컴파일러는 아무 말도 하지 않는다

        Rectangle stretched = layout.stretchToBannerWidth(adSlot);

        // 기대: 728 x 90 = 65,520
        // 실제: 728 x 728 = 529,984 — 90px 짜리 광고 슬롯이 페이지를 뚫고 내려간다
        assertThat(stretched.getHeight()).isEqualTo(728);
        assertThat(stretched.area()).isEqualTo(529_984);
    }

    @Test
    @Order(3)
    @DisplayName("3. 책의 그 코드: setW(5); setH(2); 뒤의 넓이가 Rectangle 은 10, Square 는 4")
    void bookExample() {
        assertThat(areaAfterSetting5x2(new Rectangle(1, 1))).isEqualTo(10);
        assertThat(areaAfterSetting5x2(new Square(1))).isEqualTo(4);
    }

    /** Rectangle 만 알고 작성된 코드. 어떤 객체가 들어오는지 모른다. */
    private static long areaAfterSetting5x2(Rectangle r) {
        r.setWidth(5);
        r.setHeight(2);
        return r.area();
    }

    @Test
    @Order(4)
    @DisplayName("4. [땜질] instanceof 로 Square 를 거부하면 버그는 막힌다")
    void patchRejectsSquare() {
        assertThatThrownBy(() -> patchedLayout.stretchToBannerWidth(new Square(90)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ------------------------------------------------------------------
    // 땜질의 한계 — 계약을 어기는 하위 타입은 Square 하나로 끝나지 않는다
    // ------------------------------------------------------------------

    /**
     * 편집이 잠긴 광고 영역. 잠겨 있으니 크기 변경 요청을 무시한다.
     *
     * <p>이 클래스는 <b>테스트 파일 안에</b> 있다. 즉 누군가 나중에 새로 추가한 하위 타입이다.
     * {@link BannerLayoutPatched} 는 이 타입의 존재를 모르므로 걸러내지 못한다.
     */
    private static final class LockedRectangle extends Rectangle {

        LockedRectangle(long width, long height) {
            super(width, height);
        }

        @Override
        public void setWidth(long width) {
            // 잠겨 있으므로 무시한다
        }

        @Override
        public void setHeight(long height) {
            // 잠겨 있으므로 무시한다
        }
    }

    @Test
    @Order(5)
    @DisplayName("5. [땜질의 한계] 계약을 어기는 새 하위 타입이 생기면 다시 조용히 틀린다")
    void patchMissesNewSubtype() {
        Rectangle stretched = patchedLayout.stretchToBannerWidth(new LockedRectangle(300, 90));

        // 728px 로 늘어났다고 믿고 다음 단계로 넘어가지만, 실제로는 300px 그대로다
        assertThat(stretched.getWidth()).isEqualTo(300);
        // 막으려면 BannerLayoutPatched 를 다시 열어 instanceof 를 하나 더 추가해야 한다
    }

    @Test
    @Order(6)
    @DisplayName("6. 땜질한 코드는 상위 타입만 알면 되는데 하위 타입 이름을 알게 됐다")
    void patchKnowsSubtype() {
        List<String> original = SourceCode.linesMentioning(Path.of("problem/shape/BannerLayout.java"), "Square");
        List<String> patched = SourceCode.linesMentioning(Path.of("problem/shape/BannerLayoutPatched.java"), "Square");

        System.out.println();
        System.out.println("=================================================================");
        System.out.println(" Rectangle 을 쓰는 코드가 하위 타입 Square 를 언급하는 곳");
        System.out.println("=================================================================");
        System.out.printf("  BannerLayout        : %d곳%n", original.size());
        System.out.printf("  BannerLayoutPatched : %d곳%n", patched.size());
        patched.forEach(line -> System.out.println("      " + line));
        System.out.println();
        System.out.println("  LSP 위반은 하위 타입 안에서 끝나지 않는다.");
        System.out.println("  사용하는 쪽이 instanceof 로 하위 타입을 구분하는 순간,");
        System.out.println("  하위 타입이 늘 때마다 사용하는 쪽을 고쳐야 한다 (OCP 위반으로 번진다).");
        System.out.println("=================================================================");
        System.out.println();

        assertThat(original).isEmpty();
        assertThat(patched).isNotEmpty();
    }
}

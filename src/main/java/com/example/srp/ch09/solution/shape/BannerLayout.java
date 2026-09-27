package com.example.srp.ch09.solution.shape;

import java.util.List;

/**
 * 광고 영역 레이아웃 도구.
 *
 * <p>메서드마다 <b>자기가 실제로 필요로 하는 계약</b>만 파라미터 타입으로 요구한다.
 * <ul>
 *     <li>너비만 따로 바꿔야 한다 → {@link Rectangle} 만 받는다</li>
 *     <li>넓이만 알면 된다 → 어떤 {@link Shape} 든 받는다</li>
 * </ul>
 * 그래서 {@code instanceof} 로 하위 타입을 걸러낼 일이 없다. 걸러내는 일은 타입 시스템이 한다.
 */
public class BannerLayout {

    public static final long BANNER_WIDTH = 728;

    /** 높이는 그대로 두고 너비만 배너 규격으로 늘린 새 영역을 돌려준다. */
    public Rectangle stretchToBannerWidth(Rectangle area) {
        return area.withWidth(BANNER_WIDTH);
    }

    /** 광고 영역들의 넓이 합계. 여기서는 어떤 도형이든 서로 치환할 수 있다. */
    public long totalArea(List<? extends Shape> areas) {
        return areas.stream().mapToLong(Shape::area).sum();
    }
}

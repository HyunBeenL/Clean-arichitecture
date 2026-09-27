package com.example.srp.ch09.problem.shape;

/**
 * ⚠️ {@link BannerLayout} 의 Square 버그를 "고친" 버전. 책이 경고하는 바로 그 땜질이다.
 *
 * <p>Square 를 만나면 거부하도록 {@code instanceof} 검사를 넣었다. 버그는 사라졌다.
 * 대신 더 근본적인 것이 망가졌다.
 * <ul>
 *     <li>Rectangle 만 알면 되던 코드가 이제 <b>하위 타입 Square 를 안다.</b></li>
 *     <li>계약을 어기는 하위 타입이 또 생기면 이 파일을 다시 열어야 한다. (OCP 위반으로 번진다)</li>
 *     <li>Rectangle 을 받는 <b>다른 모든 코드</b>에도 같은 검사를 넣어야 한다.</li>
 * </ul>
 *
 * <p>책의 표현을 빌리면, LSP 위반이 사용하는 쪽에 <b>별도의 예외 처리 메커니즘</b>을 강요한 것이다.
 */
public class BannerLayoutPatched {

    public Rectangle stretchToBannerWidth(Rectangle area) {
        if (area instanceof Square) {
            throw new IllegalArgumentException("정사각형 영역은 배너 너비로 늘릴 수 없습니다");
        }
        area.setWidth(BannerLayout.BANNER_WIDTH);
        return area;
    }
}

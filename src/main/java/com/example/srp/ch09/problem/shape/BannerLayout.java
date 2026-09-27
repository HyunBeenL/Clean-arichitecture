package com.example.srp.ch09.problem.shape;

/**
 * 광고 영역의 너비를 배너 규격(728px)으로 늘려주는 레이아웃 도구.
 *
 * <p>{@link Rectangle} 만 알고 작성됐다. {@link Square} 가 존재한다는 것도 모른다.
 * 그리고 그래야 정상이다. 상위 타입만 알고도 안전하게 쓸 수 있다는 것이 다형성의 약속이니까.
 *
 * <p>이 코드 자체에는 잘못이 없다. Rectangle 의 계약대로 너비만 바꿨을 뿐이다.
 * 그런데 Square 가 들어오면 높이까지 728px 로 늘어난다.
 * <b>잘못은 계약을 어긴 하위 타입에 있는데, 사고는 사용하는 쪽에서 난다.</b>
 */
public class BannerLayout {

    public static final long BANNER_WIDTH = 728;

    /** 높이는 그대로 두고 너비만 배너 규격으로 늘린다. */
    public Rectangle stretchToBannerWidth(Rectangle area) {
        area.setWidth(BANNER_WIDTH);
        return area;
    }
}

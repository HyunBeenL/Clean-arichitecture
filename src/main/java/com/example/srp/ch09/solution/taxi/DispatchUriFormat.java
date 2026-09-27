package com.example.srp.ch09.solution.taxi;

/**
 * 배차 REST URI 의 경로 이름들.
 *
 * <p>{@link #STANDARD} 가 모든 회사가 지키기로 약속한 규약이다.
 * 규약을 어긴 회사는 자기가 다르게 쓰는 항목만 {@link #with(String, String)} 으로 덮어쓴다.
 *
 * <p>여기서 분기하는 대상은 <b>URI 의 항목 이름</b>이지 회사가 아니다.
 * 항목은 규약이 정한 네 가지로 고정돼 있고, 회사가 늘어나도 이 switch 는 늘어나지 않는다.
 */
public record DispatchUriFormat(String driver, String pickupAddress, String pickupTime, String destination) {

    public static final DispatchUriFormat STANDARD =
            new DispatchUriFormat("driver", "pickupAddress", "pickupTime", "destination");

    public DispatchUriFormat with(String field, String pathName) {
        return switch (field) {
            case "driver" -> new DispatchUriFormat(pathName, pickupAddress, pickupTime, destination);
            case "pickupAddress" -> new DispatchUriFormat(driver, pathName, pickupTime, destination);
            case "pickupTime" -> new DispatchUriFormat(driver, pickupAddress, pathName, destination);
            case "destination" -> new DispatchUriFormat(driver, pickupAddress, pickupTime, pathName);
            default -> throw new IllegalArgumentException("배차 규약에 없는 항목입니다: " + field);
        };
    }
}

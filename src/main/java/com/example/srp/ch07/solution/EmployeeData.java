package com.example.srp.ch07.solution;

import java.util.List;

import com.example.srp.ch07.TimeCard;

/**
 * 직원 데이터. <b>행위(업무 규칙)가 없는 순수한 데이터 구조</b>다.
 *
 * <p>해결책의 출발점이다. 세 액터가 공통으로 쓰는 것은
 * "김개발이 이번 주에 이렇게 일했다"는 <b>사실</b>뿐이다.
 * 그 사실을 어떻게 해석할지(정규 근무시간이 몇 시간인지)는 액터마다 다르므로
 * 여기에 두지 않는다.
 */
public record EmployeeData(String name, List<TimeCard> timeCards) {

    public EmployeeData {
        timeCards = List.copyOf(timeCards);
    }
}

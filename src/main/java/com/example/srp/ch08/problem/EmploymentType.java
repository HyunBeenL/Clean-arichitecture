package com.example.srp.ch08.problem;

/**
 * 고용형태.
 *
 * <p>처음에는 {@link #FULL_TIME} 하나뿐이었고, 그다음 {@link #CONTRACT} 가 추가되었다.
 * 지금은 회계팀이 {@link #PART_TIME} 을 추가해달라고 요청한 상태다.
 *
 * <p><b>여기에 값을 하나 추가하는 것은 1초면 된다. 문제는 그 뒤다.</b>
 */
public enum EmploymentType {

    /** 정규직. */
    FULL_TIME,

    /** 계약직. */
    CONTRACT,

    /** 시간제. 회계팀이 새로 요청한 고용형태. */
    PART_TIME
}

package com.example.srp.ch08.solution;

/**
 * 고용형태 <b>하나</b>의 급여 규칙 전부.
 *
 * <p>problem 패키지에서는 "정규직"이라는 하나의 개념이
 * 네 개의 메서드에 조각조각 흩어져 있었다.
 * 여기서는 <b>구현체 한 파일이 곧 고용형태 하나</b>다.
 *
 * <pre>
 *  problem (열 단위 조직)              solution (행 단위 조직)
 *  ─────────────────────────           ─────────────────────────
 *  regularHoursLimit()  ┐              FullTimePolicy  ← 정규직 규칙 전부
 *  hourlyRate()         │ 모든 고용형태  ContractPolicy  ← 계약직 규칙 전부
 *  insuranceRate()      │ 가 뒤섞임      PartTimePolicy  ← 시간제 규칙 전부
 *  payslipTitle()       ┘
 * </pre>
 *
 * <p>고용형태를 추가한다 = 이 인터페이스를 구현한 <b>파일을 하나 새로 만든다</b>.
 * 기존 파일은 열지 않는다. 이것이 "확장에는 열려 있고 변경에는 닫혀 있다"는 뜻이다.
 */
public interface PayrollPolicy {

    /** 고용형태 코드. DB나 외부 시스템에서 넘어오는 값과 맞춘다. */
    String code();

    /** 급여명세서 제목. */
    String payslipTitle();

    /** 정규 근무시간 기준. 이 시간을 넘기면 연장 근무로 본다. */
    long regularHoursLimit();

    /** 시급. */
    long hourlyRate();

    /** 4대보험 공제율(%). 공제 대상이 아니면 0. */
    long insuranceRate();

    /** 연장 근무 시급. 기본은 시급의 1.5배이며, 필요하면 구현체가 재정의한다. */
    default long overtimeRate() {
        return hourlyRate() * 3 / 2;
    }
}

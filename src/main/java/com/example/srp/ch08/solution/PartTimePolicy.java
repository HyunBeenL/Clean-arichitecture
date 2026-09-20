package com.example.srp.ch08.solution;

/**
 * 시간제. <b>회계팀 요청으로 새로 추가한 고용형태다.</b>
 *
 * <p>problem 패키지에서는 이 고용형태 하나를 추가하려고
 * 파일 2개, 메서드 4개, 줄 9곳을 건드려야 했고 그러다 3곳을 빠뜨렸다.
 *
 * <p>여기서는 <b>이 파일 하나를 새로 만든 것이 전부다.</b>
 * {@link PayrollCalculator}, {@link PayslipPrinter},
 * {@link FullTimePolicy}, {@link ContractPolicy} 중 어느 것도 열지 않았다.
 *
 * <p>그리고 빠뜨릴 수가 없다. 메서드를 하나라도 구현하지 않으면
 * <b>컴파일이 되지 않기 때문이다.</b> (problem 의 if/else 체인과 비교해보자.)
 */
public class PartTimePolicy implements PayrollPolicy {

    @Override
    public String code() {
        return "PART_TIME";
    }

    @Override
    public String payslipTitle() {
        return "시간제 급여명세서";
    }

    /** 정규 근무시간 제한이 없다. 일한 시간 전부가 정규 근무다. */
    @Override
    public long regularHoursLimit() {
        return Long.MAX_VALUE;
    }

    @Override
    public long hourlyRate() {
        return 15_000;
    }

    /** 4대보험 공제 대상이 아니다. */
    @Override
    public long insuranceRate() {
        return 0;
    }
}

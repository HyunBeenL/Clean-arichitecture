package com.example.srp.ch08.solution;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 조립 지점(composition root).
 *
 * <p>"DB에 저장된 {@code "PART_TIME"} 이라는 문자열을 어떤 정책 객체로 바꿀 것인가"를
 * 아는 <b>유일한 한 곳</b>이다.
 *
 * <h2>솔직하게 짚고 갈 것</h2>
 * OCP는 변경 지점을 <b>0으로 만들어주지 않는다.</b> <b>한 곳으로 모아줄 뿐이다.</b>
 * 새 고용형태를 시스템 전체에서 쓰려면 {@link #defaults()} 에 한 줄을 추가해야 한다.
 * 다만 problem 에서처럼 여러 파일에 흩어진 9곳이 아니라 <b>한 파일의 한 줄</b>이고,
 * 빠뜨리면 조용히 잘못 계산되는 게 아니라 {@link NoSuchElementException} 으로 즉시 드러난다.
 *
 * <p>이 한 줄마저 없애려면 런타임 탐색이 필요하다.
 * 스프링이라면 정책들에 {@code @Component} 를 붙이고
 * {@code List<PayrollPolicy>} 를 주입받으면 되고, 순수 자바라면 {@code ServiceLoader} 를 쓴다.
 * 그때는 파일을 새로 만드는 것만으로 등록까지 끝난다.
 */
public final class PayrollPolicies {

    private final Map<String, PayrollPolicy> byCode;

    public PayrollPolicies(List<PayrollPolicy> policies) {
        this.byCode = policies.stream().collect(Collectors.toMap(
                PayrollPolicy::code,
                Function.identity(),
                (left, right) -> {
                    throw new IllegalArgumentException("고용형태 코드가 중복됩니다: " + left.code());
                },
                LinkedHashMap::new));
    }

    /** 기본으로 등록되는 고용형태들. 새 고용형태를 추가한다면 여기 한 줄이 늘어난다. */
    public static PayrollPolicies defaults() {
        return new PayrollPolicies(List.of(
                new FullTimePolicy(),
                new ContractPolicy(),
                new PartTimePolicy()));
    }

    public PayrollPolicy byCode(String code) {
        PayrollPolicy policy = byCode.get(code);
        if (policy == null) {
            throw new NoSuchElementException("등록되지 않은 고용형태입니다: " + code);
        }
        return policy;
    }

    public List<String> registeredCodes() {
        return List.copyOf(byCode.keySet());
    }
}

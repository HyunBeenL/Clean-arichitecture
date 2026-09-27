package com.example.srp.ch10;

/**
 * 세율표를 외부 세무 시스템에서 불러온다.
 *
 * <p>책 10장 후반부의 그림에서 <b>데이터베이스 D</b> 에 해당한다.
 * 서버가 죽어 있으면 {@link #load()} 가 예외를 던진다.
 */
@FunctionalInterface
public interface TaxTableLoader {

    TaxTable load();
}

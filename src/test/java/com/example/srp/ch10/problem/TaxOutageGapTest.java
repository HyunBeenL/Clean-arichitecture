package com.example.srp.ch10.problem;

import com.example.srp.ch10.Employees;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 운영팀 요구사항을 그대로 옮긴 테스트.
 *
 * <blockquote>
 *     세율표 서버는 외부 시스템이라 가끔 죽는다.
 *     그때 급여 배치가 멈추는 건 어쩔 수 없다. 하지만 <b>공지 메일과 초과근무 감시는 계속 돌아야 한다.</b>
 *     둘 다 세율표와 아무 상관이 없기 때문이다.
 * </blockquote>
 *
 * <p>{@code ContactOnlyEmployees} 같은 땜질 없이, 표준 구현인 {@link EmployeeService} 로 조립한다.
 *
 * <p>아래 {@code @Disabled} 를 지우고 실행하면 <b>빨간불</b>이 뜬다.
 * 세율표가 필요 없는 두 기능이 세율표 서버 장애에 함께 쓰러진다.
 * 쓰지도 않는 {@code calculatePay()} 를 가진 객체에 묶여 있기 때문이다.
 */
@Disabled("""
        ★ 이 @Disabled 를 지우고 실행해보세요. 테스트가 깨지는 것이 정상입니다.
           세율표가 필요 없는 기능들이 세율표 서버 장애에 함께 쓰러집니다.
        """)
@DisplayName("ch10 problem · 세율표 장애 중에도 공지 메일과 초과근무 감시는 돌아야 한다")
class TaxOutageGapTest {

    @Test
    @DisplayName("공지 메일은 계속 나간다")
    void noticeDuringOutage() {
        NoticeMailer mailer = new NoticeMailer(new EmployeeService(Employees.roster(), Employees.TAX_SERVER_DOWN));

        assertThat(mailer.send(Employees.ALL_IDS, "세율표 서버 점검 안내")).hasSize(3);
    }

    @Test
    @DisplayName("초과근무 감시도 계속 돈다")
    void overtimeDuringOutage() {
        OvertimeMonitor monitor = new OvertimeMonitor(new EmployeeService(Employees.roster(), Employees.TAX_SERVER_DOWN));

        assertThat(monitor.findOverworked(Employees.ALL_IDS)).containsExactly("E2");
    }
}

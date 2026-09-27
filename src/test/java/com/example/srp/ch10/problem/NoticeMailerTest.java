package com.example.srp.ch10.problem;

import java.util.List;

import com.example.srp.ch10.Contact;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 공지 메일 발송만 테스트하고 싶다. 필요한 건 "연락처" 하나뿐이다.
 *
 * <p>그런데 가짜 객체는 급여 계산과 근무시간까지 구현해야 한다.
 * 회계팀이 {@code calculatePay} 를 바꾸면 이 파일도 함께 고쳐야 한다.
 */
@DisplayName("ch10 problem · NoticeMailer")
class NoticeMailerTest {

    private static final class FakeDirectory implements EmployeeOperations {

        @Override
        public Contact contactOf(String employeeId) {
            return new Contact("홍길동", "hong@example.com");
        }

        @Override
        public long calculatePay(String employeeId) {
            throw new UnsupportedOperationException("이 테스트와 상관없음");
        }

        @Override
        public long weeklyHours(String employeeId) {
            throw new UnsupportedOperationException("이 테스트와 상관없음");
        }
    }

    @Test
    @DisplayName("사원마다 이름과 주소를 붙여 공지를 보낸다")
    void send() {
        NoticeMailer mailer = new NoticeMailer(new FakeDirectory());

        assertThat(mailer.send(List.of("X"), "금요일 조기 퇴근"))
                .containsExactly("To: 홍길동 <hong@example.com> | 금요일 조기 퇴근");
    }
}

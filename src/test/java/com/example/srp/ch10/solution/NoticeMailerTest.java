package com.example.srp.ch10.solution;

import java.util.List;

import com.example.srp.ch10.Contact;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** problem 의 같은 테스트와 비교해보자. 가짜 객체가 람다 한 줄이다. */
@DisplayName("ch10 solution · NoticeMailer")
class NoticeMailerTest {

    @Test
    @DisplayName("사원마다 이름과 주소를 붙여 공지를 보낸다")
    void send() {
        NoticeMailer mailer = new NoticeMailer(id -> new Contact("홍길동", "hong@example.com"));

        assertThat(mailer.send(List.of("X"), "금요일 조기 퇴근"))
                .containsExactly("To: 홍길동 <hong@example.com> | 금요일 조기 퇴근");
    }
}

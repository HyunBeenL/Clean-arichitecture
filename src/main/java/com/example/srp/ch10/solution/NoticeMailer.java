package com.example.srp.ch10.solution;

import java.util.List;

import com.example.srp.ch10.Contact;

/**
 * 사내 공지 메일 발송. <b>User3</b>.
 *
 * <p>쓰는 메서드: {@code contactOf()} 하나. 의존하는 메서드도 그 하나뿐이다.
 * 이 파일 어디에도 급여나 세율표의 흔적이 없다.
 */
public class NoticeMailer {

    private final EmployeeDirectory directory;

    public NoticeMailer(EmployeeDirectory directory) {
        this.directory = directory;
    }

    /** 공지를 보내고, 보낸 메일 목록을 돌려준다. */
    public List<String> send(List<String> employeeIds, String message) {
        return employeeIds.stream()
                .map(directory::contactOf)
                .map(contact -> format(contact, message))
                .toList();
    }

    private static String format(Contact contact, String message) {
        return "To: " + contact.name() + " <" + contact.email() + "> | " + message;
    }
}

package com.example.srp.ch10.problem;

import java.util.List;

import com.example.srp.ch10.Contact;

/**
 * 사내 공지 메일 발송. <b>User3</b>.
 *
 * <p>쓰는 메서드: {@code contactOf()} 하나.
 * 의존하는 메서드: {@link EmployeeOperations} 의 세 개 전부.
 *
 * <p>공지 메일은 급여와 아무 상관이 없다. 그런데 이 클래스를 만들려면
 * 급여 계산까지 할 줄 아는 객체가 필요하고, 그 객체는 세율표 서버가 살아 있어야 만들어진다.
 */
public class NoticeMailer {

    private final EmployeeOperations employees;

    public NoticeMailer(EmployeeOperations employees) {
        this.employees = employees;
    }

    /** 공지를 보내고, 보낸 메일 목록을 돌려준다. */
    public List<String> send(List<String> employeeIds, String message) {
        return employeeIds.stream()
                .map(employees::contactOf)
                .map(contact -> format(contact, message))
                .toList();
    }

    private static String format(Contact contact, String message) {
        return "To: " + contact.name() + " <" + contact.email() + "> | " + message;
    }
}

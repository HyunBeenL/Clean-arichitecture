package com.example.srp.ch10.solution;

import com.example.srp.ch10.Contact;

/** {@link NoticeMailer} 가 필요로 하는 것. 책의 {@code U3Ops}. */
@FunctionalInterface
public interface EmployeeDirectory {

    /** 연락처. */
    Contact contactOf(String employeeId);
}

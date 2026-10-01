package com.example.srp.ch11.problem.infra;

import java.nio.charset.StandardCharsets;

/**
 * PDF 급여명세서. <b>세부사항</b>이다.
 *
 * <p>외부 시스템이 필요 없으니 잘 동작한다. 그래도 변동성이 크다.
 * PDF 라이브러리를 바꾸거나, "PDF 말고 HTML 로 보내 주세요" 같은 요청이 언제든 들어올 수 있다.
 */
public class PdfPayslip {

    private final String employeeName;
    private final long pay;

    public PdfPayslip(String employeeName, long pay) {
        this.employeeName = employeeName;
        this.pay = pay;
    }

    public String fileName() {
        return "급여명세서-" + employeeName + ".pdf";
    }

    public byte[] toBytes() {
        return ("%PDF-1.7\n" + employeeName + " " + pay + "원").getBytes(StandardCharsets.UTF_8);
    }
}

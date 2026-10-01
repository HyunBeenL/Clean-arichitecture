package com.example.srp.ch11.solution.infra;

import java.nio.charset.StandardCharsets;

import com.example.srp.ch11.solution.payroll.Payslip;

/** PDF 급여명세서. <b>세부사항</b>이다. */
public record PdfPayslip(String employeeName, long pay) implements Payslip {

    @Override
    public String fileName() {
        return "급여명세서-" + employeeName + ".pdf";
    }

    @Override
    public byte[] content() {
        return ("%PDF-1.7\n" + employeeName + " " + pay + "원").getBytes(StandardCharsets.UTF_8);
    }
}

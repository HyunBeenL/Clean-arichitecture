package com.example.srp.ch11.problem.infra;

/**
 * SMTP 로 메일을 보낸다. <b>세부사항</b>이다.
 *
 * <p>실제라면 메일 서버에 접속한다. 이 예제 환경에는 메일 서버가 없으므로 전송이 실패한다.
 */
public class SmtpMailSender {

    private final String host;
    private final int port;

    public SmtpMailSender(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void send(String to, String attachmentName, byte[] attachment) {
        throw new IllegalStateException("SMTP 서버에 연결할 수 없습니다: " + host + ":" + port);
    }
}

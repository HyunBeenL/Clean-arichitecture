package com.example.srp.ch09.solution.taxi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 회사별 URI 형식 설정. 책이 제안한 <b>"URI를 키로 하는 설정 데이터베이스"</b>의 역할이다.
 *
 * <p>규약을 어긴 회사의 사정은 이제 <b>코드가 아니라 데이터</b>다.
 * 기본 설정은 {@code ch09/dispatch-formats.properties} 에 있고,
 * 규약을 지키는 회사는 아무것도 적지 않아도 {@link DispatchUriFormat#STANDARD} 가 적용된다.
 *
 * <h2>솔직하게 짚고 갈 것</h2>
 * 예외가 사라진 게 아니다. <b>격리된 것</b>이다.
 * 규약을 어긴 회사가 있다는 사실은 여전히 비용이고, 누군가는 이 설정을 관리해야 한다.
 * 달라진 점은 그 비용이 배차 로직을 오염시키지 않고, 설정을 바꿔도 코드를 다시 배포하지 않는다는 것이다.
 */
public final class DispatchFormats {

    private static final String DEFAULT_RESOURCE = "/ch09/dispatch-formats.properties";

    private final Map<String, DispatchUriFormat> byHost;

    public DispatchFormats(Map<String, DispatchUriFormat> byHost) {
        this.byHost = Map.copyOf(byHost);
    }

    /** 기본 설정 파일을 읽는다. */
    public static DispatchFormats load() {
        try (InputStream in = DispatchFormats.class.getResourceAsStream(DEFAULT_RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("설정 파일이 없습니다: " + DEFAULT_RESOURCE);
            }
            Properties properties = new Properties();
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
            return fromProperties(properties);
        } catch (IOException e) {
            throw new UncheckedIOException("설정 파일을 읽지 못했습니다: " + DEFAULT_RESOURCE, e);
        }
    }

    /**
     * {@code <호스트>.<항목>=<경로 이름>} 형식의 설정을 읽는다.
     * 예: {@code acme.com.destination=dest}
     */
    public static DispatchFormats fromProperties(Properties properties) {
        Map<String, DispatchUriFormat> byHost = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            int dot = key.lastIndexOf('.');
            if (dot <= 0) {
                throw new IllegalArgumentException("설정 키는 <호스트>.<항목> 형식이어야 합니다: " + key);
            }
            String host = key.substring(0, dot);
            String field = key.substring(dot + 1);
            DispatchUriFormat current = byHost.getOrDefault(host, DispatchUriFormat.STANDARD);
            byHost.put(host, current.with(field, properties.getProperty(key).trim()));
        }
        return new DispatchFormats(byHost);
    }

    public DispatchUriFormat formatFor(String dispatchBaseUri) {
        String host = URI.create(dispatchBaseUri).getHost();
        return byHost.getOrDefault(host, DispatchUriFormat.STANDARD);
    }
}

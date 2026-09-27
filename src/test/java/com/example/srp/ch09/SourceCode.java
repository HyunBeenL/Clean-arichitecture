package com.example.srp.ch09;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * 테스트가 main 소스 파일을 직접 읽어 구조를 검증할 때 쓰는 도구.
 * problem / solution 양쪽 테스트가 함께 쓴다.
 */
public final class SourceCode {

    public static final Path MAIN = Path.of("src/main/java/com/example/srp/ch09");

    private SourceCode() {
    }

    /** 주석을 제외한 코드 줄만 돌려준다. */
    public static List<String> codeLines(Path file) {
        try {
            return Files.readAllLines(MAIN.resolve(file), StandardCharsets.UTF_8).stream()
                    .map(String::trim)
                    .filter(line -> !line.startsWith("//") && !line.startsWith("*") && !line.startsWith("/*"))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("소스를 읽지 못했습니다: " + MAIN.resolve(file).toAbsolutePath(), e);
        }
    }

    /** 파일의 코드 줄 중 단어 하나라도 포함한 줄을 돌려준다. (대소문자 무시) */
    public static List<String> linesMentioning(Path file, String... words) {
        return codeLines(file).stream()
                .filter(line -> Stream.of(words).anyMatch(word -> line.toLowerCase().contains(word.toLowerCase())))
                .toList();
    }

    /** 디렉터리 안 모든 .java 파일의 코드 줄 중 단어를 포함한 줄 수. */
    public static long countInDirectory(Path directory, String word) {
        try (Stream<Path> files = Files.list(MAIN.resolve(directory))) {
            return files.filter(path -> path.toString().endsWith(".java"))
                    .map(path -> MAIN.relativize(path))
                    .flatMap(path -> codeLines(path).stream())
                    .filter(line -> line.contains(word))
                    .count();
        } catch (IOException e) {
            throw new UncheckedIOException("소스를 읽지 못했습니다: " + MAIN.resolve(directory).toAbsolutePath(), e);
        }
    }
}

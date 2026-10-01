package com.example.srp.ch11;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * 테스트가 main 소스 파일을 직접 읽어 의존 방향을 검증할 때 쓰는 도구.
 * 패키지는 {@code "problem/payroll"} 처럼 ch11 아래 경로로 지정한다.
 */
public final class SourceCode {

    private static final Path MAIN = Path.of("src/main/java/com/example/srp/ch11");
    private static final String BASE_PACKAGE = "com.example.srp.ch11.";

    private SourceCode() {
    }

    /** 패키지 안 클래스 이름들. */
    public static List<String> classNamesIn(String packagePath) {
        return javaFiles(packagePath)
                .map(path -> path.getFileName().toString().replace(".java", ""))
                .sorted()
                .toList();
    }

    /** from 패키지 파일들의 import 중 to 패키지를 가리키는 것. {@code "PayDay.java: import ...;"} 형식. */
    public static List<String> importsFrom(String fromPackage, String toPackage) {
        String target = BASE_PACKAGE + toPackage.replace('/', '.') + ".";
        return javaFiles(fromPackage)
                .flatMap(path -> codeLines(path).stream()
                        .filter(line -> line.startsWith("import ") && line.contains(target))
                        .map(line -> path.getFileName() + ": " + line))
                .sorted()
                .toList();
    }

    /** 패키지 파일들의 코드 줄(import 포함, 주석 제외) 중 단어를 하나라도 포함한 줄. */
    public static List<String> codeMentioning(String packagePath, List<String> words) {
        return javaFiles(packagePath)
                .flatMap(path -> codeLines(path).stream()
                        .filter(line -> words.stream().anyMatch(line::contains))
                        .map(line -> path.getFileName() + ": " + line))
                .sorted()
                .toList();
    }

    /** 패키지 파일 중 단어를 하나라도 언급하는 파일 이름. */
    public static List<String> filesMentioning(String packagePath, List<String> words) {
        return javaFiles(packagePath)
                .filter(path -> codeLines(path).stream().anyMatch(line -> words.stream().anyMatch(line::contains)))
                .map(path -> path.getFileName().toString())
                .sorted()
                .toList();
    }

    private static Stream<Path> javaFiles(String packagePath) {
        Path directory = MAIN.resolve(packagePath);
        try (Stream<Path> files = Files.list(directory)) {
            return files.filter(path -> path.toString().endsWith(".java")).toList().stream();
        } catch (IOException e) {
            throw new UncheckedIOException("소스를 읽지 못했습니다: " + directory.toAbsolutePath(), e);
        }
    }

    /** 주석과 package 선언을 제외한 코드 줄만 돌려준다. */
    private static List<String> codeLines(Path file) {
        try {
            return Files.readAllLines(file, StandardCharsets.UTF_8).stream()
                    .map(String::trim)
                    .filter(line -> !line.startsWith("//") && !line.startsWith("*") && !line.startsWith("/*"))
                    .filter(line -> !line.startsWith("package "))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("소스를 읽지 못했습니다: " + file.toAbsolutePath(), e);
        }
    }
}

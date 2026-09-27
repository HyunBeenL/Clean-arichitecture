package com.example.srp.ch10;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 테스트가 소스 파일과 타입 정보를 직접 읽어 구조를 검증할 때 쓰는 도구.
 * problem / solution 양쪽 테스트가 함께 쓴다.
 */
public final class SourceCode {

    private static final Path MAIN_ROOT = Path.of("src/main/java");
    private static final Path MAIN = MAIN_ROOT.resolve("com/example/srp/ch10");
    private static final Path TEST = Path.of("src/test/java/com/example/srp/ch10");

    private SourceCode() {
    }

    /**
     * 사용자 클래스가 생성자로 받는 타입의 추상 메서드 이름들.
     * 즉 이 사용자가 <b>소스 코드상으로 의존하는</b> 메서드들이다.
     */
    public static List<String> dependedMethods(Class<?> user) {
        Class<?> dependency = user.getConstructors()[0].getParameterTypes()[0];
        return Arrays.stream(dependency.getMethods())
                .filter(method -> Modifier.isAbstract(method.getModifiers()))
                .map(Method::getName)
                .sorted()
                .toList();
    }

    /** 의존하는 메서드 중 사용자 소스에서 <b>실제로 호출하는</b> 것들. */
    public static List<String> usedMethods(Class<?> user) {
        Path source = MAIN_ROOT.resolve(user.getName().replace('.', '/') + ".java");
        String code = String.join("\n", codeLines(source));
        return dependedMethods(user).stream()
                .filter(name -> code.contains("." + name + "(") || code.contains("::" + name))
                .toList();
    }

    /**
     * main·test 의 하위 패키지({@code problem} 또는 {@code solution})에서
     * 코드에 단어가 등장하는 파일 목록. 검사하는 테스트 자신은 {@code excludedClassNames} 로 뺀다.
     */
    public static List<String> filesMentioning(String subPackage, String word, String... excludedClassNames) {
        Set<String> excludedNames = Arrays.stream(excludedClassNames)
                .map(name -> name + ".java")
                .collect(Collectors.toSet());
        return Stream.concat(javaFiles(MAIN.resolve(subPackage)), javaFiles(TEST.resolve(subPackage)))
                .filter(path -> !excludedNames.contains(path.getFileName().toString()))
                .filter(path -> codeLines(path).stream().anyMatch(line -> line.contains(word)))
                .map(path -> (path.startsWith(MAIN) ? "main/" : "test/") + subPackage + "/" + path.getFileName())
                .sorted()
                .toList();
    }

    private static Stream<Path> javaFiles(Path directory) {
        try (Stream<Path> files = Files.list(directory)) {
            return files.filter(path -> path.toString().endsWith(".java")).toList().stream();
        } catch (IOException e) {
            throw new UncheckedIOException("소스를 읽지 못했습니다: " + directory.toAbsolutePath(), e);
        }
    }

    /** 주석을 제외한 코드 줄만 돌려준다. */
    private static List<String> codeLines(Path file) {
        try {
            return Files.readAllLines(file, StandardCharsets.UTF_8).stream()
                    .map(String::trim)
                    .filter(line -> !line.startsWith("//") && !line.startsWith("*") && !line.startsWith("/*"))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("소스를 읽지 못했습니다: " + file.toAbsolutePath(), e);
        }
    }
}

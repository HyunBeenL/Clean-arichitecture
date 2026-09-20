# 클린 아키텍처 7장 — 우발적 중복과 그 해결

로버트 C. 마틴 『클린 아키텍처』 **7장 「SRP: 단일 책임 원칙」** 의 `Employee` 예시를,
**테스트를 돌려서 눈으로 확인할 수 있게** 옮겨놓은 프로젝트입니다.

같은 요구사항을 두 디렉터리로 나눠 두었습니다.

| 디렉터리 | 내용 |
|---|---|
| `ch07/problem/` | ⚠️ **잘못된 예시** — 세 액터가 한 클래스에 묶여 우발적 중복이 발생한다 |
| `ch07/solution/` | ✅ **해결책** — 액터별로 클래스를 쪼개고 퍼사드로 묶는다 |

---

## 1. 7장이 말하는 SRP

흔한 오해: "클래스는 한 가지 일만 해야 한다"
→ 실제 정의: **"하나의 모듈은 하나의 액터(actor)에 대해서만 책임져야 한다"**

액터란 **그 코드의 변경을 요구하는 사람들의 집단**입니다.
`Employee` 클래스는 메서드 3개가 각각 다른 액터를 섬깁니다.

| 메서드 | 요구한 부서 | 보고 대상(액터) |
|---|---|---|
| `calculatePay()` | 회계팀 | CFO |
| `reportHours()` | 인사팀 | COO |
| `save()` | DBA | CTO |

---

## 2. 무엇이 "중복"인가 (핵심)

`calculatePay()` 와 `reportHours()` 는 둘 다 "정규 근무시간"이 필요합니다.
계산식이 같아 보이니 개발자는 당연히 중복을 제거하고 `regularHours()` 하나로 묶습니다.

```java
public long calculatePay() {
    long regularHours = regularHours();   // ← 공유
    ...
}

public String reportHours() {
    long regularHours = regularHours();   // ← 공유
    ...
}

private long regularHours() { ... }       // ⚠️ 문제의 함수
```

**여기서 놓친 것이 있습니다.** 두 부서가 말하는 "정규 근무시간"은 같은 개념이 아닙니다.

| | 회계팀(CFO) | 인사팀(COO) |
|---|---|---|
| 정규 근무시간의 의미 | 연장수당을 지급하지 않아도 되는 시간 | 근로기준법상 연장근로가 아닌 시간 |
| 바뀌는 이유 | 급여 정책이 바뀔 때 | 노동법·근태 규정이 바뀔 때 |

코드로는 한 개지만 **규칙으로는 원래 두 개**였습니다.
지금은 계산 결과가 **우연히 같을 뿐**이고, 그 우연을 보고 "중복이네" 하고 묶은 것 —
이것이 **우발적(accidental) 중복** 입니다.

> **판별 기준**
> 두 코드가 지금 똑같이 생겼더라도, **서로 다른 사람이 서로 다른 이유로 바꿀 수 있다면** 그건 중복이 아닙니다.
> 묶기 전에 물어볼 질문은 "코드가 같은가?"가 아니라 **"누가 이 코드의 변경을 요청하는가?"** 입니다.

---

## 3. 실험 시나리오

**근무 기록** (주 합계 32시간)

| 월 | 화 | 수 | 목 | 금 |
|---|---|---|---|---|
| 10h | 10h | 4h | 4h | 4h |

주 합계는 40시간을 넘지 않지만 월·화는 하루 8시간을 넘겼습니다.
**"하루 8시간 기준"과 "주 40시간 기준"의 결과가 갈리는 지점**입니다.

**회계팀의 요청 (딱 한 건)**

> "연장수당은 주 40시간을 넘긴 시간에만 지급합니다.
>  정규 근무시간을 '하루 8시간'이 아니라 '주 40시간' 기준으로 바꿔주세요."

---

## 4. `problem` — 무슨 일이 벌어지는가

개발자는 요청대로 `regularHours()` **한 곳만** 고칩니다.
→ [`EmployeeAfterCfoRequest`](src/main/java/com/example/srp/ch07/problem/EmployeeAfterCfoRequest.java)
(`calculatePay()`, `reportHours()`, `save()` 본문은 원본과 바이트 단위로 동일합니다.)

| 구분 | 변경 전 | 변경 후 | 판정 |
|---|---|---|---|
| 급여 (회계팀 요청) | 680,000원 | 640,000원 | ✅ 요청한 변경 |
| 보고서 정규시간 (인사팀) | 28시간 | **32시간** | ❌ 요청한 적 없음 |
| 보고서 연장시간 (인사팀) | 4시간 | **0시간** | ❌ 요청한 적 없음 |

실제로 있었던 **연장근로 4시간이 보고서에서 사라졌습니다.**
인사팀이 이 보고서로 연장근로 한도를 관리하고 있었다면 그대로 사고입니다.

### ★ 꼭 해볼 것

[`HrReportRegressionTest`](src/test/java/com/example/srp/ch07/problem/HrReportRegressionTest.java) 의
`@Disabled` 를 지우고 실행해 보세요.

```
Expecting actual:
  "[근무시간 보고서] 김개발
정규 근무시간: 32시간
연장 근무시간: 0시간"
to contain:
  "정규 근무시간: 28시간"
```

인사팀은 **아무 변경도 요청하지 않았는데** 인사팀 테스트가 깨집니다.
그리고 진짜 무서운 질문 — **인사팀에 이 테스트가 없었다면?**
아무도 모르는 채로 잘못된 보고서가 몇 달간 나갔을 겁니다.

---

## 5. `solution` — 어떻게 막는가

### 5-1. 데이터와 함수를 분리한다

| 클래스 | 액터 | 가진 규칙 |
|---|---|---|
| [`EmployeeData`](src/main/java/com/example/srp/ch07/solution/EmployeeData.java) | 없음 | **행위 없는 순수 데이터.** "이렇게 일했다"는 사실만 |
| [`PayCalculator`](src/main/java/com/example/srp/ch07/solution/PayCalculator.java) | CFO | 정규 근무시간 = **주 40시간** 기준 |
| [`HourReporter`](src/main/java/com/example/srp/ch07/solution/HourReporter.java) | COO | 정규 근무시간 = **하루 8시간** 기준 |
| [`EmployeeSaver`](src/main/java/com/example/srp/ch07/solution/EmployeeSaver.java) | CTO | 저장 방식 |

핵심은 `regularHours()` 가 **두 개로 갈라졌다**는 것입니다.
겉보기엔 중복이 늘어난 것 같지만, 원래 두 개였던 규칙이 제자리를 찾은 겁니다.

### 5-2. 퍼사드로 불편함만 해결한다

클래스가 셋으로 늘면 쓰는 쪽이 셋을 다 알아야 합니다.
[`EmployeeFacade`](src/main/java/com/example/srp/ch07/solution/EmployeeFacade.java) 가 그 불편만 없앱니다.
**업무 규칙은 한 줄도 갖지 않고** 생성과 위임만 합니다.

```java
EmployeeFacade employee = new EmployeeFacade("김개발", timeCards);
employee.calculatePay();   // → PayCalculator
employee.reportHours();    // → HourReporter
employee.save();           // → EmployeeSaver
```

호출하는 쪽 코드는 `problem` 과 똑같은 모양입니다. 속만 완전히 다릅니다.

### 5-3. 격리가 실제로 작동하는지 증명

`PayCalculator` 의 `REGULAR_HOURS_PER_WEEK` 를 `40` → `35` 로 바꾸고 전체 테스트를 돌리면:

```
✅ HourReporterTest      통과   ← 인사팀은 영향 없음
✅ ActorIsolationTest    통과
✅ EmployeeFacadeTest    통과
✅ EmployeeSaverTest     통과
✅ problem 패키지 전체    통과
❌ PayCalculatorTest     1건 실패   ← 회계팀 테스트만 깨진다
```

**회계팀 규칙을 바꿨더니 회계팀 테스트만 깨졌다.** 이게 SRP를 지켰을 때의 모습입니다.
`problem` 에서는 같은 변경이 인사팀을 깨뜨렸다는 걸 떠올려 보세요.

---

## 6. 실행하기

```bash
mvnw.cmd test
```

(Git Bash·PowerShell 에서는 `./mvnw test`)

`ProblemVsSolutionTest` 가 양쪽을 같은 입력으로 실제 호출해서 비교표를 출력합니다.
표의 숫자는 하드코딩이 아니라 두 구현에서 뽑아낸 실제 값입니다.

```
                              problem 패키지    solution 패키지
  -------------------------------------------------------------
  급여 (회계팀 요청)          640,000원         640,000원
  보고서 정규시간 (인사팀)    32시간            28시간
  보고서 연장시간 (인사팀)    0시간             4시간
```

### 테스트 구성

| 테스트 | 보여주는 것 |
|---|---|
| [`problem/EmployeeTest`](src/test/java/com/example/srp/ch07/problem/EmployeeTest.java) | 변경 전 상태. 두 액터의 기대가 모두 통과한다 |
| [`problem/AccidentalDuplicationTest`](src/test/java/com/example/srp/ch07/problem/AccidentalDuplicationTest.java) | 변경 전/후를 나란히 돌려 숫자가 틀어지는 것을 검증 |
| [`problem/HrReportRegressionTest`](src/test/java/com/example/srp/ch07/problem/HrReportRegressionTest.java) | **`@Disabled` 를 지우면 빨간불** |
| [`solution/PayCalculatorTest`](src/test/java/com/example/srp/ch07/solution/PayCalculatorTest.java) | 회계팀 규칙만 검증. 인사팀 이야기가 없다 |
| [`solution/HourReporterTest`](src/test/java/com/example/srp/ch07/solution/HourReporterTest.java) | 인사팀 규칙만 검증. 회계팀이 뭘 하든 통과한다 |
| [`solution/ActorIsolationTest`](src/test/java/com/example/srp/ch07/solution/ActorIsolationTest.java) | 두 규칙이 동시에 살아있음을 검증 |
| [`ProblemVsSolutionTest`](src/test/java/com/example/srp/ch07/ProblemVsSolutionTest.java) | **★ 양쪽을 같은 입력으로 실행해 비교** |

---

## 7. 생각해볼 거리

### "`totalHours()` 는 양쪽에 중복 아닌가요?"

`PayCalculator` 와 `HourReporter` 둘 다 `totalHours()` 를 갖고 있습니다.
2절의 판별 기준을 그대로 적용해 보세요.

> "총 근무시간"의 정의를 회계팀과 인사팀이 **다르게** 바꿀 수 있는가?

타임카드 시간의 합계라는 건 액터와 무관한 **사실**에 가깝습니다.
그렇다면 이건 우발적 중복이 아니라 진짜 중복일 수 있고, `EmployeeData` 로 올리거나
공용 함수로 빼도 괜찮습니다. **중요한 건 결론이 아니라 이 질문을 던졌다는 것입니다.**
`regularHours()` 때는 아무도 이 질문을 하지 않아서 사고가 났으니까요.

### 트레이드오프

책도 인정하듯 이 해결책은 공짜가 아닙니다. 클래스가 1개에서 5개로 늘었습니다.
액터가 애초에 하나뿐인 코드에까지 이 구조를 적용하면 그냥 복잡해지기만 합니다.
**기준은 "복잡한가"가 아니라 "액터가 몇 명인가"** 입니다.

### 다음 단계

7장 결론은 SRP가 상위 레벨에서 이름을 바꿔 다시 등장한다고 예고합니다.

| 수준 | 원칙 | 장 |
|---|---|---|
| 메서드·클래스 | SRP | 7장 |
| 컴포넌트·모듈 | CCP (공통 폐쇄 원칙) | 13장 |
| 아키텍처 | 변경의 축 → 경계 | 16~17장 |

---

## 환경

| 항목 | 버전 |
|---|---|
| JDK | 21 |
| Spring Boot | 4.1.0 |
| 빌드 도구 | Maven (Wrapper 포함) |
| 테스트 | JUnit 5, AssertJ |

`ch07` 패키지의 코드는 스프링에 전혀 의존하지 않는 순수 자바입니다.
(업무 규칙은 프레임워크를 몰라도 된다 — 이것도 책의 주제 중 하나죠.)

> 터미널에서 한글이 깨지면 `chcp 65001` 로 UTF-8 코드 페이지를 설정한 뒤 실행하세요.

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

---

# 8장 OCP — 개방-폐쇄 원칙 (`ch08`)

> **소프트웨어 아티팩트는 확장에는 열려 있어야 하고, 변경에는 닫혀 있어야 한다.**

평이하게 옮기면 — **기능을 추가할 때 새 파일만 만들면 되고, 기존 파일은 열 필요가 없어야 한다.**

| 디렉터리 | 내용 |
|---|---|
| `ch08/problem/` | ⚠️ **잘못된 예시** — 고용형태 분기가 여기저기 흩어져 있다 |
| `ch08/solution/` | ✅ **해결책** — 고용형태 하나가 파일 하나. 추가해도 기존 파일을 안 연다 |

## 상황

회계팀이 고용형태별 급여 규칙을 정해 두었습니다.

| | 정규 근무시간 기준 | 시급 | 4대보험 공제 | 명세서 제목 |
|---|---|---|---|---|
| 정규직 | 주 40시간 | 20,000원 | 9% 공제 | 정규직 급여명세서 |
| 계약직 | 주 35시간 | 18,000원 | 9% 공제 | 계약직 급여명세서 |
| **시간제** (신규) | **제한 없음** | **15,000원** | **공제 없음** | **시간제 급여명세서** |

정규직·계약직만 있을 때는 아무 문제가 없었습니다. 그런데 **시간제를 추가**하려는 순간 드러납니다.

## 무엇이 문제인가

`EmploymentType` enum에 값을 추가하는 건 1초면 됩니다. 문제는 그 뒤예요.
[`OcpViolationTest`](src/test/java/com/example/srp/ch08/problem/OcpViolationTest.java) 가
**소스 파일을 직접 읽어서** 고쳐야 할 곳을 세어 출력합니다.

```
 고용형태를 하나 추가하려면 손대야 하는 곳
=================================================================
  PayrollCalculator.java:45 if (type == EmploymentType.FULL_TIME) {
  PayrollCalculator.java:47 } else if (type == EmploymentType.CONTRACT) {
  PayrollCalculator.java:57 if (type == EmploymentType.FULL_TIME) {
  ...
  PayslipPrinter.java:28    if (type == EmploymentType.FULL_TIME) {
  ---------------------------------------------------------------
  -> 파일 2개, 분기 지점 9곳
```

하나의 변경이 여러 파일로 흩어지는 이 현상을 **산탄총 수술(Shotgun Surgery)** 이라고 부릅니다.

## 그래서 실제로 일어나는 일

개발자가 분기 4곳 중 **시급 한 곳만 고치고 나머지를 빠뜨렸습니다.**
그런데 **컴파일은 통과합니다.** `if/else` 체인은 처리하지 않은 값을 만나면
조용히 마지막 `return` 으로 떨어지거든요.

```
 주 45시간 근무 · 고용형태별 계산 결과
=================================================================
  고용형태          지급총액       공제액         실수령액
  -------------------------------------------------------------
  정규직            950,000원      85,500원       864,500원
  계약직            900,000원      81,000원       819,000원
  시간제 (실제)     712,500원      64,125원       648,375원
  시간제 (명세)     675,000원      0원            675,000원
```

시간제 직원에게 일어난 일:

- 있지도 않은 **연장수당**이 붙었다 (주 40시간 기준이 적용됨)
- 대상이 아닌 **4대보험 64,125원**이 빠져나갔다
- **"정규직 급여명세서"** 가 발송됐다

코드에 등장하는 횟수를 세어 보면 누락이 그대로 보입니다.

```
    FULL_TIME    4곳
    CONTRACT     4곳
    PART_TIME    1곳      <- 나머지 3곳을 빠뜨렸다
```

### ★ 꼭 해볼 것

[`PartTimeGapTest`](src/test/java/com/example/srp/ch08/problem/PartTimeGapTest.java) 의
`@Disabled` 를 지우고 실행해 보세요. 회계팀 명세 그대로 쓴 테스트 3개가 전부 깨집니다.

```
expected: 675000L but was: 712500L
expected: 0L      but was: 64125L
```

7장의 `HrReportRegressionTest` 와 같은 구조입니다.
다만 7장은 **남의 규칙을 망가뜨린 것**이었고, 8장은 **내 규칙을 확장하다 빠뜨린 것**입니다.

## 7장과의 차이

| | 문제 | 원인 |
|---|---|---|
| **7장 SRP** | 회계팀 변경이 **인사팀**을 망가뜨림 | 다른 액터끼리 코드를 공유 |
| **8장 OCP** | 회계팀이 **자기 기능**을 확장하는데 기존 파일을 다 열어야 함 | 확장 지점이 흩어져 있음 |

액터는 회계팀 하나뿐입니다. SRP는 안 깨졌어요. 그런데도 확장이 고통스럽습니다.
**SRP는 "누구의 코드인가"를, OCP는 "어떻게 늘릴 것인가"를 다룹니다.**

## 테스트 구성

| 테스트 | 보여주는 것 |
|---|---|
| [`problem/PayrollCalculatorTest`](src/test/java/com/example/srp/ch08/problem/PayrollCalculatorTest.java) | 정규직·계약직은 멀쩡히 동작한다 |
| [`problem/OcpViolationTest`](src/test/java/com/example/srp/ch08/problem/OcpViolationTest.java) | **★ 분기가 흩어진 위치를 소스에서 세어 출력 + 사고 검증** |
| [`problem/PartTimeGapTest`](src/test/java/com/example/srp/ch08/problem/PartTimeGapTest.java) | **`@Disabled` 를 지우면 빨간불** |

## `ch08/solution` — 어떻게 막는가

### 코드를 행 단위로 다시 조직한다

problem 의 문제는 **조직 축과 변경 축이 직각으로 엇갈린 것**이었습니다.
코드는 메서드(열) 단위인데 변경은 고용형태(행) 단위로 들어왔죠.
그러니 축을 맞춰주면 됩니다.

```
 problem (열 단위 조직)              solution (행 단위 조직)
 ─────────────────────────           ─────────────────────────
 regularHoursLimit()  ┐              FullTimePolicy  ← 정규직 규칙 전부
 hourlyRate()         │ 모든 고용형태  ContractPolicy  ← 계약직 규칙 전부
 insuranceRate()      │ 가 뒤섞임      PartTimePolicy  ← 시간제 규칙 전부
 payslipTitle()       ┘
```

[`PayrollPolicy`](src/main/java/com/example/srp/ch08/solution/PayrollPolicy.java) 인터페이스가
**고용형태 한 줄(행)의 계약**입니다. 구현체 한 파일이 곧 고용형태 하나예요.

| 클래스 | 내용 |
|---|---|
| [`PayrollPolicy`](src/main/java/com/example/srp/ch08/solution/PayrollPolicy.java) | 고용형태 하나가 답해야 할 다섯 가지 |
| [`FullTimePolicy`](src/main/java/com/example/srp/ch08/solution/FullTimePolicy.java) | 정규직 규칙 **전부** |
| [`ContractPolicy`](src/main/java/com/example/srp/ch08/solution/ContractPolicy.java) | 계약직 규칙 **전부** |
| [`PartTimePolicy`](src/main/java/com/example/srp/ch08/solution/PartTimePolicy.java) | 시간제 규칙 **전부** — 이 파일만 새로 만들었다 |
| [`PayrollCalculator`](src/main/java/com/example/srp/ch08/solution/PayrollCalculator.java) | 계산 절차만. **분기 0곳** |
| [`PayslipPrinter`](src/main/java/com/example/srp/ch08/solution/PayslipPrinter.java) | 출력만. **분기 0곳** |
| [`PayrollPolicies`](src/main/java/com/example/srp/ch08/solution/PayrollPolicies.java) | 조립 지점. 코드 문자열 → 정책 매핑 |

`Worker` 가 enum 대신 **정책 객체 자체**를 들고 있는 것이 열쇠입니다.
계산기는 정책에게 물어보기만 하면 되므로 분기할 일이 없어요.

```java
public Payslip calculate(Worker worker) {
    PayrollPolicy policy = worker.payrollPolicy();

    long regularHours = Math.min(worker.weeklyHours(), policy.regularHoursLimit());
    long overtimeHours = worker.weeklyHours() - regularHours;

    long grossPay = regularHours * policy.hourlyRate() + overtimeHours * policy.overtimeRate();
    long deduction = grossPay * policy.insuranceRate() / 100;
    ...
}
```

### 증거 1 — 코어에 분기가 0곳

```
 PayrollCalculator + PayslipPrinter 안의 고용형태 분기 개수
=================================================================
  problem  : 9곳   <- 고용형태가 늘면 여기를 전부 열어야 한다
  solution : 0곳   <- 고용형태가 3종이든 30종이든 이 파일들은 그대로다
```

### 증거 2 — ★ main 을 한 줄도 안 고치고 고용형태 추가

[`OpenForExtensionTest`](src/test/java/com/example/srp/ch08/solution/OpenForExtensionTest.java) 안에
**인턴(INTERN)** 이라는 완전히 새로운 고용형태를 정의해 뒀습니다.
이 클래스는 **테스트 파일 안에** 있습니다. `src/main` 아래 코드는 인턴이 생겼다는 사실조차 모릅니다.

```java
private static final class InternPolicy implements PayrollPolicy {
    public String code()              { return "INTERN"; }
    public String payslipTitle()      { return "인턴 급여명세서"; }
    public long regularHoursLimit()   { return 40; }
    public long hourlyRate()          { return 12_000; }
    public long insuranceRate()       { return 0; }
}
```

그런데도 계산기와 출력기가 아무 수정 없이 인턴 급여를 계산해냅니다.
**이게 "확장에는 열려 있다"의 실물입니다.**

### 증거 3 — 빠뜨릴 수가 없다

problem 에서 가장 위험했던 건 **분기를 빠뜨려도 컴파일이 통과한 것**이었습니다.
solution 에서는 메서드를 하나라도 구현하지 않으면 **컴파일이 안 됩니다.**
실수의 대가가 "몇 달 뒤 급여 사고"에서 "지금 당장 빨간 줄"로 바뀝니다.

### 비교

| | problem | solution |
|---|---|---|
| 새로 만드는 파일 | 0개 | 1개 |
| 열어서 고치는 파일 | 2개 | 0개 (조립 지점 제외) |
| 고쳐야 할 분기 | 9곳 | 0곳 |
| 빠뜨리면 | 조용히 잘못 계산 | 컴파일 에러 |

### 솔직하게 짚고 갈 것

**OCP는 변경 지점을 0으로 만들어주지 않습니다. 한 곳으로 모아줄 뿐입니다.**

DB에 저장된 `"PART_TIME"` 문자열을 정책 객체로 바꾸려면 어딘가는 그 매핑을 알아야 하고,
그게 [`PayrollPolicies.defaults()`](src/main/java/com/example/srp/ch08/solution/PayrollPolicies.java) 입니다.
새 고용형태를 시스템 전체에서 쓰려면 여기 **한 줄**이 늘어납니다.

다만 problem 의 흩어진 9곳과는 다릅니다.

- 한 파일의 한 줄이고
- 빠뜨리면 조용히 잘못 계산되는 게 아니라 `NoSuchElementException` 으로 즉시 드러나고
- 밖에서 조립해 넣으면 이 파일조차 안 고쳐도 됩니다

이 한 줄마저 없애려면 런타임 탐색이 필요합니다.
스프링이라면 정책에 `@Component` 를 붙이고 `List<PayrollPolicy>` 를 주입받으면 되고,
순수 자바라면 `ServiceLoader` 를 씁니다. 그때는 파일을 새로 만드는 것만으로 등록까지 끝납니다.

### 테스트 구성

| 테스트 | 보여주는 것 |
|---|---|
| [`solution/PayrollCalculatorTest`](src/test/java/com/example/srp/ch08/solution/PayrollCalculatorTest.java) | 세 고용형태 모두 명세대로. **시간제가 675,000원으로 정상** |
| [`solution/OpenForExtensionTest`](src/test/java/com/example/srp/ch08/solution/OpenForExtensionTest.java) | **★ 분기 0곳 + main 안 고치고 인턴 추가** |
| [`solution/PayrollPoliciesTest`](src/test/java/com/example/srp/ch08/solution/PayrollPoliciesTest.java) | 조립 지점. 미등록 고용형태는 예외로 즉시 드러난다 |

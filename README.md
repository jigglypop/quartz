Spring Quartz UI
=================

- JDK 17, Gradle 8.5, Spring Boot 3.2.5
- PostgreSQL 15.8, MyBatis 3.0.3, Quartz, Thymeleaf, AOP

실행 방법
---------
1. 데이터베이스 설정: `src/main/resources/application.yml`의 `spring.datasource.*`를 실제 환경으로 수정
2. 애플리케이션 실행: `./gradlew bootRun`
3. UI 접속: `http://localhost:8080`

핵심 기능
---------
- 체인 가능한 잡 스텝: `JobStep`, `ChainJobDefinition`, `ChainExecutor`
- 커스텀 에러 정책: `StopOnFirstErrorPolicy`, `SkipAndCollectErrorPolicy`
- AOP 로그: 시작/끝/소요시간/에러 - `MethodLoggingAspect`
- 관심사 분리: 스텝 정의, 체인 레지스트리, 실행 엔진 분리 구조


# quartz

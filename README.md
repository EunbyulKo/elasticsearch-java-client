

이 프로젝트는 Elasticsearch 연동과 관련된 기본 CRUD와 고급 검색 기능을 학습하고 테스트하기 위해 만든 샘플 프로젝트입니다.

---

## 요약

| 항목 | 설명 |
|------|------|
| 기본 CRUD | 문서 생성(POST), 수정(PUT), 조회(GET) API 구현 |
| 고급 검색 | 조건 검색, 페이징 처리, `search_after`과 스크롤 API 활용 |
| 테스트 | 단위 테스트(Mockito), 통합 테스트(TestContainers) |
| 로컬 테스트 | IDE HTTP 파일로 빠른 API 테스트 가능 |

---

## 구현 상세

| 기능 | 구현 방식 | 테스트 방법 | 비고 |
|------|-----------|------------|------|
| POST / PUT / GET | `Map<String, Object>` 기반 | Mockito mock으로 단위 테스트 | DTO 미사용 아쉬움 |
| 조건 검색 / 페이징 / 스크롤 | Elasticsearch Query DSL 활용 | TestContainers 테스트 | beforeAll + ServiceConnection 사용으로 static client와 Bean client 2개 활용 |
| HTTP 파일 | IntelliJ `.http` 파일 | 로컬에서 변수(`{{index}}`, `{{id}}`)로 API 테스트 가능 | Postman 대체 가능, 반복 테스트 편리 |


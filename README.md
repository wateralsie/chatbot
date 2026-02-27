# 영양성분 분석 챗봇

GPT 기반의 음식 영양성분 분석 챗봇 서버입니다.<br>
사용자가 음식 이름 및 재료를 입력하면 칼로리와 영양성분을 표 형식으로 제공합니다.

### **배포 URL**: https://chatbot-frontend-production-422c.up.railway.app

---

## 주요 기능

- **영양성분 분석**: 음식 이름 및 재료 입력 시 칼로리와 탄수화물, 단백질, 지방 등 영양소 정보 제공
- **실시간 스트리밍**: SSE(Server-Sent Events)를 통한 AI 응답 실시간 스트리밍
- **API Key 인증**: 회원가입 시 UUID 기반 API Key 발급 후 인증에 사용
- **대화 컨텍스트 유지**: 최근 10개 메시지를 기반으로 대화의 맥락 유지
- **대화 히스토리 관리**: 대화 목록 조회, 메시지 내역 조회, 대화 삭제

---

## 기술 스택

| 기술                     | 비고        |
|------------------------|-----------|
| Spring Boot 3.5.10     | Java 21   |
| OpenAI API             | GPT-4.1   |
| PostgreSQL 16          | DB        |
| Swagger                | API 문서 자동화 |
| Docker, Docker Compose | 컨테이너화     |
| Railway                | 배포        |

### Dependencies
* Spring Web
* Spring Data JPA
* Spring Security
* Spring WebFlux
* Lombok
* Validation

---

## API 명세
https://www.notion.so/API-2fc7b7c6537080db8bbdc295d2e7aca1?source=copy_link

### Swagger UI
서버 실행 후 아래 URL로 접속합니다.
```
http://localhost:8080/swagger-ui/index.html
```

---

## ERD
https://www.notion.so/ERD-2fd7b7c6537080e89c24e2c07394f6c1?source=copy_link

---

## 채팅 예시 응답

### 요청
```json
POST /api/chat/completions
X-API-Key: {api_key}

{
  "message": "계란의 영양성분을 알려줘"
}
```

### 응답
```json
{
  "success": true,
  "data": {
    "conversation_id": 1,
    "answer": "**계란** (1개 기준 / 약 50g)\n\n| 영양소 | 함량 |\n|--------|------|\n| 칼로리 | 78 kcal |\n| 탄수화물 | 0.6g |\n| 단백질 | 6.3g |\n| 지방 | 5.3g |"
  }
}
```

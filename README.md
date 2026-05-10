# POSSIBILITY TO REALITY
멋쟁이자사처럼 강남대학교 어플라이 서비스 Server 파트를 담당해요.

👉 [사용해보기](https://likelionknu.com)

<p align="center">
  <img src="https://github.com/likelionknu/apply-page-assets/blob/main/Group%2037387.png?raw=true" alt="apply-banner" width="100%" />
</p>

---
## 🛠 Tech Stack

![Java](https://img.shields.io/badge/java-21-ED8B00?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6-6DB33F?logo=springsecurity)
<br>
![JPA](https://img.shields.io/badge/Spring%20Data%20JPA-Hibernate-59666C?logo=hibernate)
![MariaDB](https://img.shields.io/badge/MariaDB-15-003545?logo=mariadb&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-8.2-DC382D?logo=redis&logoColor=white)
<br>
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?logo=githubactions&logoColor=white)

---

## 주요 기능
- 멋쟁이사자처럼 강남대학교에서 아기사자 또는 파티원을 쉽게 모집하고 관리해요
- 아기사자를 모집할 때에는 진행 상황을 실시간으로 공유하고, 이메일로 알림을 발송할 수 있어요
- 관리자 페이지에서는 모집 공고를 관리하거나, 사용자를 관리할 수 있어요

---

## 🧱 Module Structure

### Layered Architecture

```
// 프로젝트 전체 구조
├── src.main.java.com.likelionknu.applyserver
│   ├── admin
│   ├── application
│   ├── auth
│   ├── common
│   ├── discord
│   ├── mail
│   ├── recruit

// 도메인 패키지는 비즈니스 로직 담당
│   ├── admin
│   │   └── controller
│   │   └── data
│   │   └── service
│   ├── application
│   │   └── controller
│   │   └── data
│   │   └── repository
│   │   └── service
│   ├── auth
│   │   └── controller
│   │   └── data
│   │   └── repository
│   │   └── service
│   ├── recruit
│   │   └── controller
│   │   └── data
│   │   └── repository
│   │   └── service

// 인프라 관련 공통 기능 담당
│   ├── common
│   │   └── redis
│   │   └── response
│   │   └── security
│   │   └── swagger

// 외부 연동 및 알림 서비스 구성
│   ├── discord
│   │   └── data
│   │   └── service
│   ├── mail
│   │   └── data
│   │   └── service
```

---

## ERD
![ERD](https://github.com/likelionknu/apply-page-assets/blob/main/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202026-02-22%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2012.46.34.png?raw=true)

---

## 📕 Rules

### Development Process
1. 이슈 생성
2. 이슈 기반 branch(feat/#issue) 생성
3. 개발 완료 후 PR(Pull Request) 생성
4. 코드 리뷰 및 승인(2명) 후 merge

### Commit Rules
1. feat: 새 기능 추가
2. fix: 버그 수정
3. refactor: 코드 리펙토링
4. chore: 작은 수정

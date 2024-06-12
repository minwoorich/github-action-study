# MarketBridge 프로젝트 소개 _ BackEnd

자바/스프링 스터디로 이해를 높이고 실제 업무환경에서의 경험을 얻기위해 
쿠팡을 모티브로 오픈마켓 쇼핑몰 프로젝트를 구현

<br>

## 개발 인원 및  기간

- 기간 : 63일(23년 12월 26일 ~ 2월 28일)
- Back-end members : [한윤영](https://github.com/Yoonyoung-han), [이준](https://github.com/juho3416), [정민우](https://github.com/minwoorich), [유선목](https://github.com/YuSunMok), [박정인](https://github.com/Jeonginbak), [평민호](https://github.com/PyoungMinho)
- [Front-end GitHub](https://github.com/TeamObjects/Market-Bridge-FE)

<br>

## 

<br>

## 목적
- 스프링 프레임워크의 구조 및 프로젝트 실습 적용
- DB 모델링 ERD 설계 및 실습
- 웹개발 프로세스를 경험과 이해
- 팀프로젝트를 통한 프론트엔드와 백엔드간의 협업 경험

<br>

## 적용 기술 및 구현 기능


### 적용 기술

#### Spring Projects
- Spring MVC
- Spring Boot
- Spring Security
- Spring Data JPA
- QueryDSL
- Junit5
- Rest Docs
- Ascii Doctor

#### Databases
- MySQL
- H2(Test)
- Redis

#### Infrastructure
- Oracle Cloud
- Docker
- Docker Compose
- NginX
- Let's Encrypt
- Github Actions
- Cloud Flare

#### Monitoring
- Spring Actuators
- Prometheus
- Grafana

#### Third Party
- kakaopay

#### Data 수집
- [Scrapy](https://github.com/TeamObjects/crawler)

#### 협업툴
- git
- notion
- discord


<br>

### DB Modeling
![](https://velog.velcdn.com/images/jeongin/post/36640df1-ccc9-4ca1-acd2-81bddd2a6937/image.png)


### 구현 기능

#### 공통
- 스크래피 프레임워크 활용하여 쿠팡 데이터 크롤링
- 반환 형식을 포맷팅한 공통 APIResponse 구현
- @CreateDate, DateTime을 테스트하기 위한 공통 DateTimeHolder 구현
- 단위 테스트를 위한 TestContainer 구현
- Restdocs, Ascii doctor를 통해 API Docs 작성
- 로깅 시스템 구축
  logback 프레임워크와 스프링AOP를 활용하여 사용자의 요청부터 서버의 응답까지 전체적인 데이터의 로그로 출력, 파일로 저장
- 스프링 AOP를 활용하여 글로벌 예외처리 핸들러와 에러 전용 변환 포멧 객체 구현

#### Member
- 인증, 인가를 위해 Spring Security, Redis를 활용하여 JWT RefreshToken 방식을 구현
- JPA 쿼리메소드를 사용한 주소 CRUD 구현
- 카카오페이 외부 API를 연결하여 멤버쉽 구독서비스 구현 (스프링 스케줄러 사용)
- QueryDSL을 이용한 위시리스트 컬렉션 조회 join과 페이징 구현
- JPA 양방향 관계를 이용한 쿠폰 리스트 조회

#### Product
- 계층형 구조의 카테고리 데이터 처리
  QueryDSL를 활용하여 데이터를 조회 후 자체 구현한 helper를 통해 계층형 구조로 변환 
- 상품 조회 
  재귀쿼리를 사용하여 상위 카테고리에 해당되는 전체 상품 조회
  JPA 페이징을 활용하여 페이지네이션 구현
- JPA 양방향 관계를 이용한 제품 옵션, 이미지, 태그 정보를 생성 및 조회
- JPA 쿼리메소드, JPQL을 사용하여 리뷰 CRUD 구현.
- 리뷰 좋아요를 upsert(없으면 생성, 있으면 삭제) 구현
- 리뷰 CRUD 구현
  리뷰 등록시 JPA 연관관계 매핑을 활용하여 구현
  리뷰 수정, 삭제시 JPA 변강감지를 통해 반영되도록 구현
  리뷰 조회시 페이지네이션, 좋아요/최신순 정렬 구현

#### Order
- 확장성(OCP)을 고려한 결제 서드파티 추상화하여 카카오페이 API를 통해 결제취소 구현
- 반품/취소 생성 구현
  Spring Data JPA 쿼리메소드 훨용하여 쿼리를 구현
  취소와 반품의 플로우 중복되는 부분을 전략패턴을 활용하여 코드 중복을 제거
- QueryDSL을 활용하여 반품/취소 리스트 페이지네이션 구현
- Lock을 통한 재고처리 동시성 코드 구현
- JPA와 QueryDSL 을 활용하여 장바구니 CRUD 를 구현
- JPA와 QueryDSL 을 활용하여 주문 CRUD 를 구현하였습니다.
- 카카오페이 API를 연동하여 결제시스템을 구현했습니다.

#### 인프라
- Oracle Cloud의 컴퓨터 인스턴스 (ubuntu22.04)를 생성하여 원격 서버 생성
- Github Actions, Docker, Docker-compose를 사용 한 CI/CD 구축
- HTTPS 사용을 위해 cloud flare에서 도메인을 구입하여 DNS 등록하고
  let's encrypt(certbot)을 통해 인증서 발급
  Nginx를 실행하여 Https와 Http간의 요청/응답을 처리

<br>

## [API documents(RestDOCS 문서)](https://objects-mb.com/docs/index.html)

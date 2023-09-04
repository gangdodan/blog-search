

# Application 실행
- application 실행을 위해 아래 두가지 요건을 우선적으로 확인해야 합니다.
### 1. Open API
  - [이곳](https://github.com/gangdodan/20230705_2126-000316/releases/tag/v.1.0) 에서 API 키를 발급 받아야 합니다. 
  - 직접 빌드 시
    - `src/main/resources/application.yml` 의 open.kako.REST_API_KEY의 {API_KEY} 를 발급받은 API 키로 변경합니다.
  - 빌드 파일 실행 시
    - [이곳](https://github.com/gangdodan/20230705_2126-000316/releases/tag/v.1.0) 에서 jar 파일을 다운받고 다운받은 파일이 있는 위치에서 아래 명령어를 실행합니다.
    - `java -jar blog-0.0.1-SNAPSHOT.jar --spring.profiles.active=local --open.kako.REST_API_KEY={발급 API키}`
### 2. Embedded Redis
  - jar 파일 단독 실행 환경을 위해 Embedded Redis를 사용하였습니다. 실행중인 Redis 서버가 있으면 application 실행이 실패할 수 있으니 서버를 종료 후 실행시켜주세요.
<br>

# *관련 문서
### 1. API 명세 및 API call test
- Run in Postman을 통해 API call 테스트를 수행하고 API 명세를 확인할 수 있습니다.
- https://documenter.getpostman.com/view/22569968/2s93zE418z

### 2. Slack log
- 슬랙 채널을 생성하여 Incoming WebHooks 앱을 추가합니다.
- 생성된 webhook url을 appilication.yml의 logging.slack.webhook-url에 작성하면 Exception log를 실시간으로 수신할 수 있습니다.


<br>

# 프로젝트 설계
- 초기 멀티 모듈(core/api/batch)로 구현 시도하였으나 application feature 볼륨에 따라 단일 모듈 구조로 변경
- Directory
  - 상위 : Domain,Common 컴포넌트
  - 하위 : 레이어 구분에 따라 크게 Business Logic/Infrastructure/API 구분

<br>

# 기능 설명

### 외부 API 핸들링 
- 원본 데이터를 가공하여 필요한 데이터만 추출하여 Response
- 필요 시 타사 API로 교체할 수 있도록 API 호출 로직 추상화
### 검색어 랭킹
- 동작 : 1일 단위로 데이터 갱신 → 스케쥴러를 통해 전일 데이터 제거
- 추후 변경될 수 있는 랭킹 업데이트 스펙 혹은 대용량 트래픽 처리에 대비하여 캐싱 및 비동기식 처리 모델을 사용하는 Redis로 데이터 관리

### 예외 상황 대응
- Redis 장애 및 오류 발생 시 DB(H2) 데이터를 바탕으로 랭킹 집계하는 로직 구현
- 외부 API 의존 시 장애 대응 
  - 특정 API에만 의존하지 않도록 Circuit Breaker 전략 적용 필요

### 공통
- 상수 관리 
  - 교체 가능성이 있거나 자주 사용되는 value는 Constants로 관리
- Exception 핸들링 
  - 로그 파일과 별개로 Exception 로그를 slack으로 실시간 push 받을 수 있도록 webhook 설정 
  - 클라이언트가 일관된 에러 정보를 받아볼 수 있도록 ExceptionHandler작성

### 미구현
- 동시성 이슈 대응



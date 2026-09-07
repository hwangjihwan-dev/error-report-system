# DB 설계서

## 1. 테이블 목록

| 테이블명 | 설명         |
|---|------------|
| users | 사용자 정보     |
| error_reports | 오류신고 정보    |
| error_report_history | 오류신고 처리 이력 |

---

## 2. users 테이블

사용자 정보를 저장하는 테이블이다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGINT | PK, 자동 증가 | 사용자 ID |
| login_id | VARCHAR(50) | NOT NULL, UNIQUE | 로그인 아이디 |
| password | VARCHAR(255) | NOT NULL | 암호화된 비밀번호 |
| name | VARCHAR(50) | NOT NULL | 사용자 이름 |
| role | VARCHAR(20) | NOT NULL | 사용자 권한 |
| created_at | TIMESTAMP | NOT NULL | 가입일시 |

### 컬럼 설명

- `id`: 사용자를 식별하기 위한 기본키이며 자동 증가 값으로 관리한다.
- `login_id`: 사용자가 로그인할 때 입력하는 아이디이며 중복될 수 없다.
- `password`: 암호화된 비밀번호를 저장한다.
- `name`: 사용자 이름을 저장한다.
- `role`: 사용자 권한을 저장한다. 기본값은 `USER`이다.
- `created_at`: 회원가입 일시를 저장한다.

---

## 3. error_reports 테이블

사용자가 등록한 오류신고 정보를 저장하는 테이블이다.

| 컬럼명 | 타입           | 제약조건 | 설명 |
|---|--------------|---|---|
| id | BIGINT       | PK, 자동 증가 | 오류신고 ID |
| user_id | BIGINT       | FK, NOT NULL | 작성자 ID |
| title | VARCHAR(100) | NOT NULL | 제목 |
| content | TEXT         | NOT NULL | 내용 |
| status | VARCHAR(20)  | NOT NULL | 처리상태 |
| answer | TEXT         | NULL | 관리자 답변 |
| reject_reason | TEXT         | NULL | 반려 사유 |
| admin_id | BIGINT       | FK, NULL | 답변한 관리자 ID |
| answered_at | TIMESTAMP    | NULL | 답변일시 |
| original_file_name | VARCHAR(255) | NULL | 원본 첨부파일명 |
| stored_file_name | VARCHAR(255) | NULL | 저장 첨부파일명 |
| file_path | VARCHAR(255) | NULL | 첨부파일 저장 경로 |
| file_size | BIGINT | NULL | 첨부파일 크기 |
| created_at | TIMESTAMP    | NOT NULL | 등록일시 |
| updated_at | TIMESTAMP    | NULL | 수정일시 |

### 컬럼 설명

- `id`: 오류신고를 식별하기 위한 기본키이며 자동 증가 값으로 관리한다.
- `user_id`: 오류신고를 작성한 사용자의 `users.id`를 참조한다.
- `title`: 오류신고 제목을 저장한다.
- `content`: 오류신고 내용을 저장한다.
- `status`: 오류신고 처리상태를 저장한다.
- `answer`: 관리자가 등록한 답변 내용을 저장한다.
- `reject_reason`: 관리자가 오류신고를 반려 처리한 사유를 저장한다.
- `admin_id`: 답변을 등록한 관리자의 `users.id`를 참조한다.
- `answered_at`: 관리자 답변 등록일시를 저장한다.
- `original_file_name`: 사용자가 업로드한 첨부파일의 원본 파일명을 저장한다.
- `stored_file_name`: 서버에 저장된 첨부파일명을 저장한다.
- `file_path`: 첨부파일이 저장된 서버 경로를 저장한다.
- `file_size`: 첨부파일 크기를 저장한다.
- `created_at`: 오류신고 등록일시를 저장한다.
- `updated_at`: 오류신고 수정일시 또는 상태 변경일시를 저장한다.

---

## 4. error_report_history 테이블

오류신고의 관리자 처리 이력을 저장하는 테이블이다.

| 컬럼명 | 타입          | 제약조건 | 설명 |
|---|-------------|---|---|
| id | BIGINT      | PK, 자동증가 | 처리 이력 ID |
| report_id | BIGINT      | FK, NOT NULL | 오류신고 ID |
| admin_id | BIGINT      | FK, NOT NULL | 처리 관리자  ID |
| previous_status | VARCHAR(20) | NOT NULL | 변경 전 처리상태 |
| new_status | VARCHAR(20) | NOT NULL | 변경 후 처리상태 |
| comment | TEXT | NULL | 답변 내용 또는 반려 사유 |
| created_at | TIMESTAMP | NOT NULL | 처리 이력 생성 일시 |

### 컬럼 설명

- `report_id`: 처리 이력이 발생한 오류신고의 `error_reports.id`를 참조한다.
- `admin_id`: 처리 이력을 생성한 관리자의 `users.id`를 참조한다.
- `previous_status`: 변경 전 처리상태를 저장한다.
- `new_status`: 변경 후 처리상태를 저장한다.
- `comment`: 답변 내용 또는 반려 사유를 저장한다.
- `created_at`: 처리 이력이 생성된 일시를 저장한다.

## 5. 테이블 관계

### users 1 : N error_reports

사용자 1명은 여러 개의 오류신고를 작성할 수 있다.

오류신고 1건은 반드시 작성자 1명을 가진다.

- `users.id = error_reports.user_id`

### users 1 : N error_reports(admin_id)

관리자 1명은 여러 개의 오류신고에 답변할 수 있다.

오류신고 1건은 답변한 관리자 1명을 가질 수 있다.

단, 답변이 등록되기 전에는 답변한 관리자가 없으므로 `admin_id`는 `NULL`일 수 있다.

- `users.id = error_reports.admin_id`

### error_reports 1 : N error_report_history

오류신고 1건은 여러 개의 처리 이력을 가질 수 있다.

- `error_reports.id = error_report_history.report_id`

### users 1: N error_report_history(admin_id)

관리자 1명은 여러 개의 처리 이력을 생성할 수 있다.

- `users_id = error_report_history.admin_id`  

### 관계 설명

- `users.id`는 사용자 테이블의 기본키이다.
- `users.login_id`는 사용자가 로그인할 때 입력하는 아이디이다.
- `error_reports.user_id`는 오류신고 작성자를 나타내는 외래키이다.
- `error_reports.user_id`는 `users.id`를 참조한다.
- `error_reports.admin_id`는 답변을 등록한 관리자를 나타내는 외래키이다.
- `error_reports.admin_id`는 `users.id`를 참조한다.

---

## 6. 처리상태 값

| 상태값         | 한글명 | 설명 |
|-------------|---|---|
| RECEIVED    | 접수  | 사용자가 오류신고를 등록한 초기 상태 |
| IN_PROGRESS | 확인중      | 관리자가 오류 내용을 확인 중인 상태 |
| COMPLETED   | 처리완료        | 관리자가 답변을 등록하고 처리를 완료한 상태 |
| REJECTED    | 반려          | 오류신고 내용이 부적절하거나 처리 대상이 아닌 상태 |

---

## 7. 제약조건

### users

- `id`는 기본키이며 자동 증가 값으로 관리한다.
- `login_id`는 중복될 수 없다.
- `password`는 반드시 저장되어야 한다.
- `name`은 반드시 저장되어야 한다.
- `role`은 반드시 저장되어야 한다.
- 회원가입 시 기본 권한은 `USER`로 설정한다.

### error_reports

- `id`는 기본키이며 자동 증가 값으로 관리한다.
- 오류신고는 반드시 작성자를 가져야 한다.
- `user_id`는 `users.id`를 참조한다.
- 제목은 반드시 입력되어야 한다.
- 내용은 반드시 입력되어야 한다.
- 처리상태는 반드시 저장되어야 한다.
- 최초 처리상태는 `접수`로 설정한다.
- 관리자 답변이 등록되기 전까지 `answer`, `admin_id`, `answered_at`은 `NULL`일 수 있다.
- 관리자 답변이 등록되면 `answer`, `admin_id`, `answered_at`을 함께 저장한다.
- 처리상태는 `RECEIVED`, `IN_PROGRESS`, `COMPLETED`, `REJECTED` 중 하나여야 한다.
- 반려 처리 시 `reject_reason`, `admin_id`, `answered_at`을 함께 저장한다.
- 첨부파일이 등록된 경우 `original_file_name`, `stored_file_name`, `file_path`, `file_size`를 저장한다.

### error_report_history

- `id`는 기본키이며 자동 증가 값으로 관리한다.
- 처리 이력은 반드시 오류신고를 참조해야 한다.
- 처리 이력은 반드시 처리 관리자를 참조해야 한다.
- 변경 전 처리상태와 변경 후 처리상태는 반드시 저장되어야 한다.
- 처리 이력 생성일시는 반드시 저장되어야 한다.

---

## 8. 인덱스 고려사항

목록 조회와 검색 성능을 고려하여 아래 컬럼에 인덱스 적용을 검토한다.

| 테이블명 | 컬럼명 | 목적 |
|---|---|---|
| users | login_id | 로그인 아이디 중복 확인 및 로그인 조회 |
| error_reports | user_id | 사용자별 오류신고 목록 조회 |
| error_reports | admin_id | 관리자별 답변 이력 조회 |
| error_reports | status | 처리상태별 조회 |
| error_reports | created_at | 최신순 목록 조회 |

---

## 9. 향휴 확장 예정 테이블

현재 첨부파일 정보는 `error_reports` 테이블의 첨부파일 컬럼으로 관리한다.
추후 다중 첨부파일 기능이 필요할 경우 별도의 `attachments` 테이블 분리를 고려한다.

| 테이블명 | 설명 |
|---|---|
| attachments | 다중 첨부파일 관리를 위한 첨부파일 정보 |
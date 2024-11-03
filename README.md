## 공지사항 API
공지사항을 등록/조회/수정/삭제하는 API 개발.

## 주요 스펙
- Java 21
- Spring Boot 3.3.5
- WAS : Undertow - 2.3.17
- DB : MySQL 8.0.40

## 핵심 문제 해결 전략
- JPA를 사용하여 데이터베이스와의 상호작용 간소화
- RESTFUL API 기준에 맞추어 다른 사용자들이 쉽게 사용할 수 있도록 개발
- Controller, Service, Repository 계층을 분리하여 역할을 분리하고, 트랜잭션을 구분함
- ExceptionController나 AOP같은 정형화 된 구조는 없지만, 가능한 통일성 있는 응답을 줄 수 있도록 Response 클래스를 활용함

## 실행 방법
1. MySQL 설치 (<https://www.mysql.com/downloads/>)
2. DB, 테이블 생성
  - database : notice
  - host : localhost
  - port : 3306
  - user : root
  - password : root
  - url : jdbc:mysql://localhost:3306/notice
```mysql
    create table if not exists notice.notice
(
    notice_id             bigint auto_increment
        primary key,
    title                 varchar(255)   null,
    content               varchar(10000) null,
    author                varchar(255)   null,
    end_date_time         datetime(3)    null,
    start_date_time       datetime(3)    null,
    views                 bigint         null,
    delete_flag           varchar(1)     null,
    created_by            varchar(255)   null,
    creation_date_time    datetime(3)    null,
    last_updated_by       varchar(255)   null,
    last_update_date_time datetime(3)    null
);

create table if not exists notice.notice_file
(
    notice_file_id     bigint auto_increment
        primary key,
    notice_id          bigint       null,
    file_name          varchar(255) null,
    file_path          varchar(255) null,
    delete_flag        varchar(1)   null,
    deleted_by         varchar(255) null,
    delete_date_time   datetime(3)  null,
    created_by         varchar(255) null,
    creation_date_time datetime(3)  null,
    constraint FKki5xdltri56drlbp92rsbhhq
        foreign key (notice_id) references notice.notice (notice_id)
);
```
3. NoticeApplication 실행 (서버 실행)
4. 아래 API를 참고하여 API 기능 테스트(Postman 등을 활용)

## 공지사항 API 명세
### 공지사항 등록
- URL : http://localhost:8080/api/notice
- Mothod : POST
- Body : form-data
- Request Parameter

| 변수명 | 설명 | 최대 길이 | 필수여부 | Content-Type |
| :---: | :---: | :---: | :---: | :---: |
| title | 제목 | 255 | Y | application/json |
| content | 내용 | 10000 | Y | application/json |
| author | 작성자 | 255 | Y | application/json |
| startDateTime | 공지 시작일 | - | Y | application/json |
| endDateTime | 공지 종료일 | - | Y | application/json |
| files[] | 첨부파일 | 최대10개 | N | multipart/form-data |

- Response

| 처리 결과 | 응답 내용(resultMessage) |
| :---: | :---: |
| 성공 | 공지 등록에 성공했어요. |
| 첨부파일 경로 생성 오류 | 첨부파일 경로 생성 중 오류가 발생했어요. |
| 파일 저장 오류 | 파일 저장에 실패했어요. |

```json
{
    "statusCode": "OK",                                   - 상태값
    "resultMessage": "공지 등록에 성공했어요.",             - 응답 내용
    "resultObject": {                                     - 결과 Data
        "createdBy": "author",                            - 작성자
        "creationDateTime": "2024-11-01T00:00:00.000",    - 작성시간
        "lastUpdatedBy": "author",                        - 최종 수정자
        "lastUpdateDateTime": "2024-11-01T00:00:00.000",  - 최종 수정 시간
        "noticeId": 20,                                   - 공지사항ID(PK)
        "title": "title",                                 - 제목
        "content": "content",                             - 내용
        "author": "author",                               - 작성자
        "startDateTime": "2024-01-01T00:00:00",           - 공지 시작일
        "endDateTime": "2025-01-01T00:00:00",             - 공지 종료일
        "views": 0,                                       - 조회수
        "deleteFlag": "N",                                - 삭제여부
        "pageNo": null,                                   - 페이지 번호
        "pageSize": null,                                 - 페이지 당 갯수
        "noticeFiles": null                               - 첨부파일
    }
}
```

### 공지사항 수정
- URL : http://localhost:8080/api/notice
- Mothod : PUT
- Body : form-data
- Request Parameter

| 변수명 | 설명 | 최대 길이 | 필수여부 | Content-Type |
| :---: | :---: | :---: | :---: | :---: |
| title | 제목 | 255 | Y | application/json |
| content | 내용 | 10000 | Y | application/json |
| author | 작성자 | 255 | Y | application/json |
| startDateTime | 공지 시작일 | - | Y | application/json |
| endDateTime | 공지 종료일 | - | Y | application/json |
| files[] | 첨부파일 | 최대10개 | N | multipart/form-data |
| noticeId | 공지사항ID | - | Y | application/json |

- Response

| 처리 결과 | 응답 내용(resultMessage) |
| :---: | :---: |
| 성공 | 공지 수정에 성공했어요. |
| 첨부파일 경로 생성 오류 | 첨부파일 경로 생성 중 오류가 발생했어요. |
| 파일 저장 오류 | 파일 저장에 실패했어요. |

```json
{
    "statusCode": "OK",                                   - 상태값
    "resultMessage": "공지 수정에 성공했어요.",            - 응답 내용
    "resultObject": {                                     - 결과 Data
        "createdBy": "null",                              - 작성자
        "creationDateTime": "null",                       - 작성시간
        "lastUpdatedBy": "null",                          - 최종 수정자
        "lastUpdateDateTime": "null",                     - 최종 수정 시간
        "noticeId": 20,                                   - 공지사항ID(PK)
        "title": "title",                                 - 제목
        "content": "content",                             - 내용
        "author": "author",                               - 작성자
        "startDateTime": "2024-01-01T00:00:00",           - 공지 시작일
        "endDateTime": "2025-01-01T00:00:00",             - 공지 종료일
        "views": 5,                                       - 조회수
        "deleteFlag": "N",                                - 삭제여부
        "pageNo": null,                                   - 페이지 번호
        "pageSize": null,                                 - 페이지 당 갯수
        "noticeFiles": null                               - 첨부파일
    }
}
```

### 공지사항 삭제
- URL : http://localhost:8080/api/notice
- Mothod : DELETE
- Body : raw
- 작성자 동일여부 확인 후 삭제 가능
- Request Parameter 

| 변수명 | 설명 | 최대 길이 | 필수여부 | Content-Type |
| :---: | :---: | :---: | :---: | :---: |
| noticeId | 공지사항ID | - | Y | application/json |
| author | 작성자 | 255 | Y | application/json |

- Response

| 처리 결과 | 응답 내용(resultMessage) |
| :---: | :---: |
| 성공 | 삭제가 완료되었어요. |
| 작성자가 다른 경우 | 공지사항 작성자가 달라요. 작성자가 같은 경우에만 삭제할 수 있어요. |

```json
{
    "statusCode": "OK",                                   - 상태값
    "resultMessage": "삭제가 완료되었어요.",                - 응답 내용
    "resultObject": null
}
```

### 공지사항 조회(목록)
- URL : http://localhost:8080/api/notice
- Mothod : GET
- Body : raw
- Request Parameter

| 변수명 | 설명 | 타입 | 기본값 | 필수여부 | Content-Type |
| :---: | :---: | :---: | :---: | :---: | :---: |
| pageNo | 페이지 번호 | 자연수 | 1 | N | application/json |
| pageSize | 페이지 당 공지사항 갯수 | 자연수 | 10 | N | application/json |

- Response
```json{
    "statusCode": "OK",
    "resultMessage": "조회가 완료 되었어요.",
    "resultObject": [
        {
            "content": "content",
            "noticeId": 1,
            "deleteFlag": "N",
            "views": 9,
            "author": "author",
            "title": "title",
            "creationDateTime": "2024-11-01T01:00:00.000",
            "startDateTime": "2024-01-01T00:00:00",
            "endDateTime": "2025-01-01T00:00:00"
        },
        {
            "content": "content",
            "noticeId": 2,
            "deleteFlag": "N",
            "views": 0,
            "author": "author",
            "title": "title",
            "creationDateTime": "2024-11-01T01:00:00.000",
            "startDateTime": "2024-01-01T00:00:00",
            "endDateTime": "2025-01-01T00:00:00"
        },
        {
            "content": "content",
            "noticeId": 3,
            "deleteFlag": "N",
            "views": 0,
            "author": "author",
            "title": "title",
            "creationDateTime": "2024-11-01T01:00:00.000",
            "startDateTime": "2024-01-01T00:00:00",
            "endDateTime": "2025-01-01T00:00:00"
        },
        {
            "content": "content",
            "noticeId": 4,
            "deleteFlag": "N",
            "views": 0,
            "author": "author",
            "title": "title",
            "creationDateTime": "2024-11-01T01:00:00.000",
            "startDateTime": "2024-01-01T00:00:00",
            "endDateTime": "2025-01-01T00:00:00"
        },
        {
            "content": "content",
            "noticeId": 5,
            "deleteFlag": "N",
            "views": 0,
            "author": "author",
            "title": "title",
            "creationDateTime": "2024-11-01T01:00:00.000",
            "startDateTime": "2024-01-01T00:00:00",
            "endDateTime": "2025-01-01T00:00:00"
        }
    ]
}
```

### 공지사항 조회(상세/단건)
- URL : http://localhost:8080/api/notice/{noticeId}
- Mothod : GET
- Content-Type : application/json

- Response
```json
{
    "statusCode": "OK",
    "resultMessage": "조회가 완료 되었어요.",
    "resultObject": {
        "createdBy": "author",
        "creationDateTime": "2024-11-01T00:00:00.000",
        "lastUpdatedBy": "author",
        "lastUpdateDateTime": "2024-11-01T00:00:00.000",
        "noticeId": 21,
        "title": "title",
        "content": "content",
        "author": "author",
        "startDateTime": "2024-01-01T00:00:00",
        "endDateTime": "2025-01-01T00:00:00",
        "views": 29,
        "deleteFlag": "N",
        "pageNo": null,
        "pageSize": null,
        "noticeFiles": [
            {
                "fileName": "image1.jpg",
                "noticeFileId": 21
            },
            {
                "fileName": "image2.jpg",
                "noticeFileId": 22
            }
        ]
    }
}
```

### 첨부파일 다운로드
- URL : http://localhost:8080/api/notice/files/{noticeFileId}/{fileName}
- Mothod : GET

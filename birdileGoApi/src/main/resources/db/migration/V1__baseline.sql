create table bg_address
(
    address_id      bigint auto_increment comment '주소ID'
        primary key,
    road_address    varchar(200)                          null comment '도로명주소',
    landlot_address varchar(200)                          null comment '지번주소',
    extra_address   varchar(200)                          null comment '상세주소',
    latitude        decimal(10, 8)                        null comment '위도',
    longitude       decimal(11, 8)                        null comment '경도',
    created_at      timestamp default current_timestamp() null
)
    comment '주소' collate = utf8mb4_unicode_ci;

create index idx_coordinates
    on bg_address (latitude, longitude);

create table bg_code
(
    code_type   varchar(50)                           not null comment '코드 그룹 (e.g., MATCH_TYPE, CATEGORY, AGE_GROUP, SKILL_LEVEL, ROUND)',
    code        varchar(50)                           not null comment '코드 값',
    description varchar(100)                          not null comment '코드 설명',
    sort_order  int       default 0                   null comment '정렬 순서',
    use_yn      char      default 'Y'                 null comment '사용 여부',
    created_at  timestamp default current_timestamp() null,
    primary key (code_type, code)
)
    comment '통합 코드' collate = utf8mb4_unicode_ci;

create index idx_code_type
    on bg_code (code_type);

create index idx_use_yn
    on bg_code (use_yn);

create table bg_participant
(
    participant_id    bigint auto_increment comment '참가자 아아디'
        primary key,
    participant_name  varchar(100)                          not null comment '참가자 명',
    phone             varchar(20)                           null comment '전화번호',
    email             varchar(100)                          null comment '이메일',
    participant_role  varchar(20)                           not null comment 'PLAYER, REFEREE 등',
    club_name         varchar(50)                           null comment '클럽명',
    created_at        timestamp default current_timestamp() null,
    updated_at        timestamp                             null on update current_timestamp(),
    gender            varchar(10)                           null comment '성별 (남/여/혼합복식용)',
    birth_date        date                                  null comment '생년월일 (연령 그룹 분류용)',
    skill_level       varchar(20)                           null comment '실력 수준',
    emergency_contact varchar(100)                          null comment '비상 연락처'
)
    comment '참가자' collate = utf8mb4_unicode_ci;

create index idx_club_name
    on bg_participant (club_name);

create index idx_email
    on bg_participant (email);

create index idx_participant_name
    on bg_participant (participant_name);

create index idx_participant_role
    on bg_participant (participant_role);

create index idx_phone
    on bg_participant (phone);

create table bg_participant_upload_log
(
    upload_id      bigint auto_increment comment '업로드 아아디'
        primary key,
    uploader       varchar(100)                            not null comment '업로더',
    original_name  varchar(200)                            not null comment '원파일명',
    log_status     varchar(20) default 'PENDING'           null comment 'PENDING / COMPLETED / FAILED',
    error_message  text                                    null comment '에러 메시지',
    file_size      bigint                                  null comment '파일 크기 (bytes)',
    processed_rows int         default 0                   null comment '처리된 행 수',
    uploaded_at    timestamp   default current_timestamp() null,
    completed_at   timestamp                               null comment '완료시간'
)
    comment '엑셀 업로드 로그 테이블' collate = utf8mb4_unicode_ci;

create index idx_log_status
    on bg_participant_upload_log (log_status);

create index idx_uploaded_at
    on bg_participant_upload_log (uploaded_at);

create table bg_permission
(
    perm_id    bigint auto_increment
        primary key,
    resource   varchar(100)                          not null comment '예: /api/matches',
    action     varchar(20)                           not null comment '예: READ, WRITE, DELETE',
    perm_desc  varchar(200)                          null comment '권한 설명',
    created_at timestamp default current_timestamp() null,
    constraint uk_resource_action
        unique (resource, action)
)
    comment '퍼미션 테이블' collate = utf8mb4_unicode_ci;

create index idx_resource
    on bg_permission (resource);

create table bg_role
(
    role_id    bigint auto_increment
        primary key,
    role_name  varchar(50)                           not null,
    role_desc  varchar(200)                          null comment '롤 설명',
    created_at timestamp default current_timestamp() null,
    constraint role_name
        unique (role_name)
)
    collate = utf8mb4_unicode_ci;

create index idx_role_name
    on bg_role (role_name);

create table bg_role_perm
(
    role_id    bigint                                not null,
    perm_id    bigint                                not null,
    granted_at timestamp default current_timestamp() null,
    primary key (role_id, perm_id),
    constraint fk_role_perm_perm
        foreign key (perm_id) references bg_permission (perm_id)
            on delete cascade,
    constraint fk_role_perm_role
        foreign key (role_id) references bg_role (role_id)
            on delete cascade
)
    comment '롤↔퍼미션 매핑' collate = utf8mb4_unicode_ci;

create table bg_user
(
    user_id       bigint auto_increment comment '유저 아이디'
        primary key,
    login_id      varchar(50)                            not null comment '로그인 아이디',
    display_name  varchar(100)                           not null comment '화면 표시용 이름',
    password      varchar(255)                           not null comment '비밀번호',
    enabled       tinyint(1) default 1                   null comment '유저 상태',
    contact_phone varchar(20)                            null comment '연락처',
    email         varchar(100)                           null comment '이메일',
    company_name  varchar(100)                           null comment '회사명',
    position      varchar(100)                           null comment '직위',
    job_level     varchar(50)                            null comment '직급',
    created_at    timestamp  default current_timestamp() null,
    updated_at    timestamp                              null on update current_timestamp(),
    constraint login_id
        unique (login_id)
)
    comment '사용자' collate = utf8mb4_unicode_ci;

create table bg_event
(
    event_id           bigint auto_increment comment '대회 아이디'
        primary key,
    creator_id         bigint                                  not null comment '등록자 ID',
    event_name         varchar(100)                            not null comment '대회 명칭',
    region             varchar(100)                            not null comment '대회 지역',
    venue              varchar(200)                            not null comment '대회 장소',
    address_id         bigint                                  null comment '주소 ID 참조',
    start_date         date                                    not null comment '대회 시작일',
    end_date           date                                    not null comment '대회 종료일',
    registration_start date                                    not null comment '접수 시작일',
    registration_end   date                                    not null comment '접수 종료일',
    contact_phone      varchar(20)                             null comment '문의 전화번호',
    image_info         varchar(255)                            null comment '첨부 사진 정보',
    created_at         timestamp   default current_timestamp() null comment '등록 일자',
    updated_at         timestamp                               null on update current_timestamp() comment '수정 일자',
    spone_id           varchar(50)                             null comment '스포넷아이디',
    image_url          varchar(255)                            null comment '이미지 주소',
    event_status       varchar(20) default 'READY'             null comment '대회 상태',
    prize_info         text                                    null comment '상금/상품 정보',
    entry_fee          decimal(10, 2)                          null comment '참가비',
    max_participants   int                                     null comment '최대 참가자 수',
    event_description  text                                    null comment '대회 상세 설명',
    constraint fk_bg_event_address
        foreign key (address_id) references bg_address (address_id),
    constraint fk_bg_event_creator
        foreign key (creator_id) references bg_user (user_id)
)
    comment '대회 정보' collate = utf8mb4_unicode_ci;

create index idx_creator_id
    on bg_event (creator_id);

create index idx_event_dates
    on bg_event (start_date, end_date);

create index idx_event_status
    on bg_event (event_status);

create index idx_region
    on bg_event (region);

create index idx_registration_dates
    on bg_event (registration_start, registration_end);

create table bg_event_sponsor
(
    event_id     bigint                                  not null,
    sponsor_name varchar(100)                            not null comment '스폰서명',
    sponsor_type varchar(20) default 'GENERAL'           null comment '스폰서 유형',
    sponsor_url  varchar(255)                            null comment '스폰서 웹사이트',
    created_at   timestamp   default current_timestamp() null,
    primary key (event_id, sponsor_name),
    constraint fk_event_sponsor_event
        foreign key (event_id) references bg_event (event_id)
            on delete cascade
)
    comment '대회 스폰서' collate = utf8mb4_unicode_ci;

create table bg_match
(
    match_id       bigint auto_increment comment '경기 아이디'
        primary key,
    event_id       bigint                                  not null comment '대회 아이디',
    match_datetime datetime                                not null comment '경기 시간',
    start_time     datetime                                null comment '경기 시작시간',
    end_time       datetime                                null comment '경기 종료시간',
    court_no       varchar(20)                             null,
    match_type     varchar(20)                             not null comment '경기 타입',
    category       varchar(20)                             not null comment '경기 카테코기',
    age_group      varchar(10)                             not null comment '경기 나이',
    match_level    varchar(10)                             null comment '경기 급수',
    match_status   varchar(20) default 'SCHEDULED'         null comment '경기 상태',
    created_at     timestamp   default current_timestamp() null,
    updated_at     timestamp                               null on update current_timestamp(),
    round_info     varchar(50)                             null comment '토너먼트 라운드 (예선, 16강, 8강 등)',
    group_name     varchar(20)                             null comment '조 이름 (리그전의 경우)',
    match_order    int                                     null comment '경기 순서',
    constraint fk_bg_match_event
        foreign key (event_id) references bg_event (event_id)
            on delete cascade
)
    comment '경기' collate = utf8mb4_unicode_ci;

create index idx_category
    on bg_match (category);

create index idx_court_no
    on bg_match (court_no);

create index idx_match_datetime
    on bg_match (match_datetime);

create index idx_match_event
    on bg_match (event_id);

create index idx_match_status
    on bg_match (match_status);

create index idx_match_type
    on bg_match (match_type);

create table bg_match_result
(
    match_id        bigint                                 not null comment '경기 아아디'
        primary key,
    winner_team_no  int                                    null comment '승리팀 번호 (1, 2, NULL for draw)',
    total_sets      int        default 0                   null comment '총 세트',
    team1_sets_won  int        default 0                   null comment '팀1 승리 세트',
    team2_sets_won  int        default 0                   null comment '팀2 승리 세트',
    match_completed tinyint(1) default 0                   null comment '경기 완료 여부',
    completed_at    timestamp                              null comment '경기 완료 시간',
    updated_at      timestamp  default current_timestamp() null on update current_timestamp(),
    constraint fk_match_result_match
        foreign key (match_id) references bg_match (match_id)
            on delete cascade,
    constraint chk_winner_team
        check (`winner_team_no` in (1, 2) or `winner_team_no` is null)
)
    comment '경기 결과 요약 테이블 (성능 최적화용)' collate = utf8mb4_unicode_ci;

create index idx_match_completed
    on bg_match_result (match_completed);

create index idx_winner_team
    on bg_match_result (winner_team_no);

create table bg_match_set_score
(
    set_score_id bigint auto_increment comment '경기 세트 아이디'
        primary key,
    match_id     bigint                                not null comment '경기 아이디',
    set_number   int                                   not null comment '세트 번호 (1, 2, 3)',
    team1_score  int       default 0                   not null,
    team2_score  int       default 0                   not null,
    set_winner   int                                   null comment '세트 승자 팀 번호 (1 or 2)',
    created_at   timestamp default current_timestamp() null,
    updated_at   timestamp                             null on update current_timestamp(),
    constraint uk_match_set
        unique (match_id, set_number),
    constraint fk_set_score_match
        foreign key (match_id) references bg_match (match_id)
            on delete cascade,
    constraint chk_set_number
        check (`set_number` > 0),
    constraint chk_set_winner
        check (`set_winner` in (1, 2) or `set_winner` is null)
)
    comment '경기 세트 점수' collate = utf8mb4_unicode_ci;

create index idx_match_id
    on bg_match_set_score (match_id);

create table bg_match_team
(
    match_team_id bigint auto_increment comment '경기 팀 아이디'
        primary key,
    match_id      bigint                                not null comment '경기 아이디',
    team_no       int                                   not null comment '1 or 2',
    team_name     varchar(100)                          null comment '팀명',
    created_at    timestamp default current_timestamp() null,
    constraint uk_match_team
        unique (match_id, team_no),
    constraint fk_bg_team_match
        foreign key (match_id) references bg_match (match_id)
            on delete cascade,
    constraint chk_team_no
        check (`team_no` in (1, 2))
)
    comment '경기 팀' collate = utf8mb4_unicode_ci;

create table bg_match_member
(
    match_member_id bigint auto_increment comment '팀소속참가지 아이디'
        primary key,
    match_team_id   bigint                                 not null comment '팀 경기 아이디',
    participant_id  bigint                                 null comment '팀 참가자 아이디',
    is_main         tinyint(1) default 1                   null,
    member_role     varchar(20)                            null comment '복식에서의 역할 (예: 주장, 서브 순서 등)',
    created_at      timestamp  default current_timestamp() null,
    constraint fk_bg_member_participant
        foreign key (participant_id) references bg_participant (participant_id),
    constraint fk_bg_member_team
        foreign key (match_team_id) references bg_match_team (match_team_id)
            on delete cascade
)
    comment '팀 소속 참가자 테이블' collate = utf8mb4_unicode_ci;

create index idx_match_team_id
    on bg_match_member (match_team_id);

create index idx_participant_id
    on bg_match_member (participant_id);

create index idx_match_id
    on bg_match_team (match_id);

create table bg_refresh_token
(
    token_id   bigint auto_increment
        primary key,
    user_id    bigint                                 not null,
    token      varchar(512)                           not null,
    issued_at  timestamp  default current_timestamp() null,
    expires_at timestamp                              not null,
    revoked    tinyint(1) default 0                   null,
    revoked_at timestamp                              null,
    user_agent varchar(500)                           null comment '사용자 에이전트 정보',
    ip_address varchar(45)                            null comment 'IP 주소',
    constraint token
        unique (token),
    constraint fk_refresh_token_user
        foreign key (user_id) references bg_user (user_id)
            on delete cascade
)
    comment 'Refresh 토큰 저장' collate = utf8mb4_unicode_ci;

create index idx_expires_at
    on bg_refresh_token (expires_at);

create index idx_revoked
    on bg_refresh_token (revoked);

create index idx_token
    on bg_refresh_token (token);

create index idx_user_id
    on bg_refresh_token (user_id);

create index idx_email
    on bg_user (email);

create index idx_enabled
    on bg_user (enabled);

create index idx_login_id
    on bg_user (login_id);

create table bg_user_role
(
    user_id     bigint                                not null,
    role_id     bigint                                not null,
    assigned_at timestamp default current_timestamp() null,
    primary key (user_id, role_id),
    constraint fk_user_role_role
        foreign key (role_id) references bg_role (role_id)
            on delete cascade,
    constraint fk_user_role_user
        foreign key (user_id) references bg_user (user_id)
            on delete cascade
)
    comment '사용자↔롤 매핑' collate = utf8mb4_unicode_ci;


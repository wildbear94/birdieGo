CREATE TABLE `bg_address` (
  `address_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '주소ID',
  `road_address` varchar(200) DEFAULT NULL COMMENT '도로명주소',
  `landlot_address` varchar(200) DEFAULT NULL COMMENT '지번주소',
  `extra_address` varchar(200) DEFAULT NULL COMMENT '상세주소',
  `latitude` decimal(10,8) DEFAULT NULL COMMENT '위도',
  `longitude` decimal(11,8) DEFAULT NULL COMMENT '경도',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`address_id`),
  KEY `idx_coordinates` (`latitude`,`longitude`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='주소'

CREATE TABLE `bg_code` (
  `code_type` varchar(50) NOT NULL COMMENT '코드 그룹 (e.g., MATCH_TYPE, CATEGORY, AGE_GROUP, SKILL_LEVEL, ROUND)',
  `code` varchar(50) NOT NULL COMMENT '코드 값',
  `description` varchar(100) NOT NULL COMMENT '코드 설명',
  `sort_order` int(11) DEFAULT 0 COMMENT '정렬 순서',
  `use_yn` char(1) DEFAULT 'Y' COMMENT '사용 여부',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`code_type`,`code`),
  KEY `idx_code_type` (`code_type`),
  KEY `idx_use_yn` (`use_yn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='통합 코드'

CREATE TABLE `bg_event` (
  `event_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '대회 아이디',
  `creator_id` bigint(20) NOT NULL COMMENT '등록자 ID',
  `event_name` varchar(100) NOT NULL COMMENT '대회 명칭',
  `region` varchar(100) NOT NULL COMMENT '대회 지역',
  `venue` varchar(200) NOT NULL COMMENT '대회 장소',
  `address_id` bigint(20) DEFAULT NULL COMMENT '주소 ID 참조',
  `start_date` date NOT NULL COMMENT '대회 시작일',
  `end_date` date NOT NULL COMMENT '대회 종료일',
  `registration_start` date NOT NULL COMMENT '접수 시작일',
  `registration_end` date NOT NULL COMMENT '접수 종료일',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '문의 전화번호',
  `image_info` varchar(255) DEFAULT NULL COMMENT '첨부 사진 정보',
  `created_at` timestamp NULL DEFAULT current_timestamp() COMMENT '등록 일자',
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp() COMMENT '수정 일자',
  `spone_id` varchar(50) DEFAULT NULL COMMENT '스포넷아이디',
  `image_url` varchar(255) DEFAULT NULL COMMENT '이미지 주소',
  `event_status` varchar(20) DEFAULT 'READY' COMMENT '대회 상태',
  `prize_info` varchar(255)  DEFAULT NULL COMMENT '상금/상품 정보',
  `entry_fee` decimal(10,2) DEFAULT NULL COMMENT '참가비',
  `max_participants` int(11) DEFAULT NULL COMMENT '최대 참가자 수',
  `event_description` varchar(255)  DEFAULT NULL COMMENT '대회 상세 설명',
  PRIMARY KEY (`event_id`),
  KEY `fk_bg_event_address` (`address_id`),
  KEY `idx_event_status` (`event_status`),
  KEY `idx_event_dates` (`start_date`,`end_date`),
  KEY `idx_registration_dates` (`registration_start`,`registration_end`),
  KEY `idx_creator_id` (`creator_id`),
  KEY `idx_region` (`region`),
  CONSTRAINT `fk_bg_event_address` FOREIGN KEY (`address_id`) REFERENCES `bg_address` (`address_id`),
  CONSTRAINT `fk_bg_event_creator` FOREIGN KEY (`creator_id`) REFERENCES `bg_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='대회 정보'

CREATE TABLE `bg_event_sponsor` (
  `event_id` bigint(20) NOT NULL,
  `sponsor_name` varchar(100) NOT NULL COMMENT '스폰서명',
  `sponsor_type` varchar(20) DEFAULT 'GENERAL' COMMENT '스폰서 유형',
  `sponsor_url` varchar(255) DEFAULT NULL COMMENT '스폰서 웹사이트',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`event_id`,`sponsor_name`),
  CONSTRAINT `fk_event_sponsor_event` FOREIGN KEY (`event_id`) REFERENCES `bg_event` (`event_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='대회 스폰서'

CREATE TABLE `bg_match` (
  `match_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '경기 아이디',
  `event_id` bigint(20) NOT NULL COMMENT '대회 아이디',
  `match_datetime` datetime NOT NULL COMMENT '경기 시간',
  `start_time` datetime DEFAULT NULL COMMENT '경기 시작시간',
  `end_time` datetime DEFAULT NULL COMMENT '경기 종료시간',
  `court_no` varchar(20) DEFAULT NULL,
  `match_type` varchar(20) NOT NULL COMMENT '경기 타입',
  `category` varchar(20) NOT NULL COMMENT '경기 카테코기',
  `age_group` varchar(10) NOT NULL COMMENT '경기 나이',
  `match_level` varchar(10) DEFAULT NULL COMMENT '경기 급수',
  `match_status` varchar(20) DEFAULT 'SCHEDULED' COMMENT '경기 상태',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp(),
  `round_info` varchar(50) DEFAULT NULL COMMENT '토너먼트 라운드 (예선, 16강, 8강 등)',
  `group_name` varchar(20) DEFAULT NULL COMMENT '조 이름 (리그전의 경우)',
  `match_order` int(11) DEFAULT NULL COMMENT '경기 순서',
  PRIMARY KEY (`match_id`),
  KEY `idx_match_event` (`event_id`),
  KEY `idx_match_datetime` (`match_datetime`),
  KEY `idx_match_status` (`match_status`),
  KEY `idx_match_type` (`match_type`),
  KEY `idx_category` (`category`),
  KEY `idx_court_no` (`court_no`),
  CONSTRAINT `fk_bg_match_event` FOREIGN KEY (`event_id`) REFERENCES `bg_event` (`event_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='경기'

CREATE TABLE `bg_match_member` (
  `match_member_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '팀소속참가지 아이디',
  `match_team_id` bigint(20) NOT NULL COMMENT '팀 경기 아이디',
  `participant_id` bigint(20) DEFAULT NULL COMMENT '팀 참가자 아이디',
  `is_main` tinyint(1) DEFAULT 1,
  `member_role` varchar(20) DEFAULT NULL COMMENT '복식에서의 역할 (예: 주장, 서브 순서 등)',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`match_member_id`),
  KEY `idx_match_team_id` (`match_team_id`),
  KEY `idx_participant_id` (`participant_id`),
  CONSTRAINT `fk_bg_member_participant` FOREIGN KEY (`participant_id`) REFERENCES `bg_participant` (`participant_id`),
  CONSTRAINT `fk_bg_member_team` FOREIGN KEY (`match_team_id`) REFERENCES `bg_match_team` (`match_team_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='팀 소속 참가자 테이블'

CREATE TABLE `bg_match_result` (
  `match_id` bigint(20) NOT NULL COMMENT '경기 아아디',
  `winner_team_no` int(11) DEFAULT NULL COMMENT '승리팀 번호 (1, 2, NULL for draw)',
  `total_sets` int(11) DEFAULT 0 COMMENT '총 세트',
  `team1_sets_won` int(11) DEFAULT 0 COMMENT '팀1 승리 세트',
  `team2_sets_won` int(11) DEFAULT 0 COMMENT '팀2 승리 세트',
  `match_completed` tinyint(1) DEFAULT 0 COMMENT '경기 완료 여부',
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '경기 완료 시간',
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`match_id`),
  KEY `idx_winner_team` (`winner_team_no`),
  KEY `idx_match_completed` (`match_completed`),
  CONSTRAINT `fk_match_result_match` FOREIGN KEY (`match_id`) REFERENCES `bg_match` (`match_id`) ON DELETE CASCADE,
  CONSTRAINT `chk_winner_team` CHECK (`winner_team_no` in (1,2) or `winner_team_no` is null)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='경기 결과 요약 테이블 (성능 최적화용)'

CREATE TABLE `bg_match_set_score` (
  `set_score_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '경기 세트 아이디',
  `match_id` bigint(20) NOT NULL COMMENT '경기 아이디',
  `set_number` int(11) NOT NULL COMMENT '세트 번호 (1, 2, 3)',
  `team1_score` int(11) NOT NULL DEFAULT 0,
  `team2_score` int(11) NOT NULL DEFAULT 0,
  `set_winner` int(11) DEFAULT NULL COMMENT '세트 승자 팀 번호 (1 or 2)',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp(),
  PRIMARY KEY (`set_score_id`),
  UNIQUE KEY `uk_match_set` (`match_id`,`set_number`),
  KEY `idx_match_id` (`match_id`),
  CONSTRAINT `fk_set_score_match` FOREIGN KEY (`match_id`) REFERENCES `bg_match` (`match_id`) ON DELETE CASCADE,
  CONSTRAINT `chk_set_winner` CHECK (`set_winner` in (1,2) or `set_winner` is null),
  CONSTRAINT `chk_set_number` CHECK (`set_number` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='경기 세트 점수'

CREATE TABLE `bg_match_team` (
  `match_team_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '경기 팀 아이디',
  `match_id` bigint(20) NOT NULL COMMENT '경기 아이디',
  `team_no` int(11) NOT NULL COMMENT '1 or 2',
  `team_name` varchar(100) DEFAULT NULL COMMENT '팀명',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`match_team_id`),
  UNIQUE KEY `uk_match_team` (`match_id`,`team_no`),
  KEY `idx_match_id` (`match_id`),
  CONSTRAINT `fk_bg_team_match` FOREIGN KEY (`match_id`) REFERENCES `bg_match` (`match_id`) ON DELETE CASCADE,
  CONSTRAINT `chk_team_no` CHECK (`team_no` in (1,2))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='경기 팀'

CREATE TABLE `bg_participant` (
  `participant_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '참가자 아아디',
  `participant_name` varchar(100) NOT NULL COMMENT '참가자 명',
  `phone` varchar(20) DEFAULT NULL COMMENT '전화번호',
  `email` varchar(100) DEFAULT NULL COMMENT '이메일',
  `participant_role` varchar(20) NOT NULL COMMENT 'PLAYER, REFEREE 등',
  `club_name` varchar(50) DEFAULT NULL COMMENT '클럽명',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp(),
  `gender` varchar(10) DEFAULT NULL COMMENT '성별 (남/여/혼합복식용)',
  `birth_date` date DEFAULT NULL COMMENT '생년월일 (연령 그룹 분류용)',
  `skill_level` varchar(20) DEFAULT NULL COMMENT '실력 수준',
  `emergency_contact` varchar(100) DEFAULT NULL COMMENT '비상 연락처',
  PRIMARY KEY (`participant_id`),
  KEY `idx_participant_name` (`participant_name`),
  KEY `idx_participant_role` (`participant_role`),
  KEY `idx_club_name` (`club_name`),
  KEY `idx_email` (`email`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='참가자'

CREATE TABLE `bg_participant_upload_log` (
  `upload_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '업로드 아아디',
  `uploader` varchar(100) NOT NULL COMMENT '업로더',
  `original_name` varchar(200) NOT NULL COMMENT '원파일명',
  `log_status` varchar(20) DEFAULT 'PENDING' COMMENT 'PENDING / COMPLETED / FAILED',
  `error_message` text DEFAULT NULL COMMENT '에러 메시지',
  `file_size` bigint(20) DEFAULT NULL COMMENT '파일 크기 (bytes)',
  `processed_rows` int(11) DEFAULT 0 COMMENT '처리된 행 수',
  `uploaded_at` timestamp NULL DEFAULT current_timestamp(),
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '완료시간',
  PRIMARY KEY (`upload_id`),
  KEY `idx_log_status` (`log_status`),
  KEY `idx_uploaded_at` (`uploaded_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='엑셀 업로드 로그 테이블'

CREATE TABLE `bg_permission` (
  `perm_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `resource` varchar(100) NOT NULL COMMENT '예: /api/matches',
  `action` varchar(20) NOT NULL COMMENT '예: READ, WRITE, DELETE',
  `perm_desc` varchar(200) DEFAULT NULL COMMENT '권한 설명',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`perm_id`),
  UNIQUE KEY `uk_resource_action` (`resource`,`action`),
  KEY `idx_resource` (`resource`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='퍼미션 테이블'

CREATE TABLE `bg_refresh_token` (
  `token_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `token` varchar(512) NOT NULL,
  `issued_at` timestamp NULL DEFAULT current_timestamp(),
  `expires_at` timestamp NOT NULL,
  `revoked` tinyint(1) DEFAULT 0,
  `revoked_at` timestamp NULL DEFAULT NULL,
  `user_agent` varchar(500) DEFAULT NULL COMMENT '사용자 에이전트 정보',
  `ip_address` varchar(45) DEFAULT NULL COMMENT 'IP 주소',
  PRIMARY KEY (`token_id`),
  UNIQUE KEY `token` (`token`),
  KEY `idx_token` (`token`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_expires_at` (`expires_at`),
  KEY `idx_revoked` (`revoked`),
  CONSTRAINT `fk_refresh_token_user` FOREIGN KEY (`user_id`) REFERENCES `bg_user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Refresh 토큰 저장'

CREATE TABLE `bg_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) NOT NULL,
  `role_desc` varchar(200) DEFAULT NULL COMMENT '롤 설명',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_name` (`role_name`),
  KEY `idx_role_name` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci

CREATE TABLE `bg_role_perm` (
  `role_id` bigint(20) NOT NULL,
  `perm_id` bigint(20) NOT NULL,
  `granted_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`role_id`,`perm_id`),
  KEY `fk_role_perm_perm` (`perm_id`),
  CONSTRAINT `fk_role_perm_perm` FOREIGN KEY (`perm_id`) REFERENCES `bg_permission` (`perm_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_role_perm_role` FOREIGN KEY (`role_id`) REFERENCES `bg_role` (`role_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='롤↔퍼미션 매핑'

CREATE TABLE `bg_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '유저 아이디',
  `login_id` varchar(50) NOT NULL COMMENT '로그인 아이디',
  `display_name` varchar(100) NOT NULL COMMENT '화면 표시용 이름',
  `password` varchar(255) NOT NULL COMMENT '비밀번호',
  `enabled` tinyint(1) DEFAULT 1 COMMENT '유저 상태',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '연락처',
  `email` varchar(100) DEFAULT NULL COMMENT '이메일',
  `company_name` varchar(100) DEFAULT NULL COMMENT '회사명',
  `position` varchar(100) DEFAULT NULL COMMENT '직위',
  `job_level` varchar(50) DEFAULT NULL COMMENT '직급',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp(),
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `login_id` (`login_id`),
  KEY `idx_login_id` (`login_id`),
  KEY `idx_enabled` (`enabled`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자'

CREATE TABLE `bg_user_role` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  `assigned_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `fk_user_role_role` (`role_id`),
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `bg_role` (`role_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `bg_user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자↔롤 매핑'


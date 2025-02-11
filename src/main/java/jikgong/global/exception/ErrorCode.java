package jikgong.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    /**
     * member
     */
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-001", "회원 정보가 없습니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "MEMBER-002", "만료된 access token 입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "MEMBER-003", "만료된 refresh token 입니다."),
    REFRESH_TOKEN_NOT_MATCH(HttpStatus.FORBIDDEN, "MEMBER-004", "유효하지 않은 refresh token 입니다. 다시 로그인하세요."),
    MEMBER_PHONE_EXIST(HttpStatus.CONFLICT, "MEMBER-005", "이미 등록된 핸드폰 번호입니다."),
    MEMBER_INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "MEMBER-006", "비밀번호가 올바르지 않습니다."),
    MEMBER_LOGIN_ID_EXIST(HttpStatus.CONFLICT, "MEMBER-007", "이미 등록된 id입니다."),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-008", "요청된 회원의 ROLE을 특정할 수 없습니다."),
    MEMBER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "MEMBER-009", "인증이 필요합니다."),
    MEMBER_FORBIDDEN(HttpStatus.FORBIDDEN, "MEMBER-010", "권한이 필요합니다."),
    MEMBER_CONSENTS_NEED_TO_AGREE(HttpStatus.FORBIDDEN, "MEMBER-011", "회원가입을 위해선 각종 내용에 동의해야합니다."),
    MEMBER_INVALID_AUTH_CODE(HttpStatus.BAD_REQUEST, "MEMBER-012", "인증 코드가 올바르지 않습니다."),
    MEMBER_NOT_STAY_WITH_IN_THE_COUNTRY(HttpStatus.BAD_REQUEST, "MEMBER-013", "현재 체류 중인 외국인이 아닙니다."),
    MEMBER_UPDATE_WORKER_CARD_FAIL(HttpStatus.BAD_REQUEST, "MEMBER-014", "노동자 카드 정보 업데이트에 실패했습니다."),

    /**
     * 알림
     */
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION-001", "알림 정보가 없습니다."),
    FCM_FIREBASE_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "FCM-001", "회원 정보에 FCM 토큰이 없습니다."),
    FCM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FCM-002", "FCM 알림 전송 중 에러가 발생했습니다."),

    /**
     * 좋아요
     */
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "LIKE-001", "좋아요 누른 정보가 없습니다."),
    LIKE_REQUEST_INVALID(HttpStatus.BAD_REQUEST, "LIKE-002", "좋아요는 기업이 노동자에게 누를 수 있습니다."),
    LIKE_ALREADY_EXIST(HttpStatus.CONFLICT, "LIKE-003", "해당 회원에겐 이미 좋아요를 눌렀습니다."),

    /**
     * 이미지, 파일, 경력 증명서
     */
    FILE_NOT_FOUND_EXTENSION(HttpStatus.BAD_REQUEST, "FILE-001", "확장자를 찾을 수 없습니다."),
    FILE_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "FILE-002", "지원하지 않는 확장자 입니다."),
    FILE_STREAM_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE-001", "이미지 처리 중 서버에서 에러가 발생했습니다."),
    THUMBNAIL_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "IMAGE-002", "썸네일 이미지를 찾을 수 없습니다."),
    CERTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "CERTIFICATION-001", "회원의 증명서 정보가 없습니다."),

    /**
     * 위치
     */
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "LOCATION-001", "위치 정보가 없습니다."),
    LOCATION_ALREADY_MAIN(HttpStatus.CONFLICT, "LOCATION-002", "이미 메인 위치로 등록된 위치입니다."),

    /**
     * 수익
     */
    PROFIT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROFIT-001", "수익 정보가 없습니다."),

    /**
     * 요청
     */
    APPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "APPLY-001", "요청 정보가 없습니다."),
    APPLY_ALREADY_EXIST(HttpStatus.CONFLICT, "APPLY-002", "이미 신청한 모집 공고 입니다."),
    APPLY_OVER_RECRUIT_NUM(HttpStatus.BAD_REQUEST, "APPLY-003", "모집 인원을 초과했습니다."),
    APPLY_ALREADY_ACCEPTED_IN_WORK_DATE(HttpStatus.CONFLICT, "APPLY-004", "이미 승인된 지원 내역이 있습니다."),
    APPLY_CAN_TEN_MINUTE_AGO(HttpStatus.BAD_REQUEST, "APPLY-005", "일자리 신청은 출역 시간 10분 전 까지 가능합니다."),
    APPLY_CANCEL_IMPOSSIBLE(HttpStatus.BAD_REQUEST, "APPLY-006", "취소가 불가한 내역입니다."),
    APPLY_OFFERED_NOT_FOUND(HttpStatus.BAD_REQUEST, "APPLY-007", "제안 받으며 자동으로 생성된 지원 내역이 없습니다."),
    APPLY_NEED_TO_PENDING(HttpStatus.BAD_REQUEST, "APPLY-008", "대기 중인 요청에 대해서만 처리할 수 있습니다."),
    APPLY_ACCEPT_NEED_TO_PAST(HttpStatus.BAD_REQUEST, "APPLY-009", "요청 승인은 출역 시각 이전만 가능합니다.."),

    /**
     * 모집 공고 관련 (JobPost, WorkDate, Project)
     */
    JOB_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "JOBPOST-001", "모집 공고 정보가 없습니다."),
    JOB_POST_DELETE_FAIL(HttpStatus.CONFLICT, "JOBPOST-002", "확정된 지원자가 있을 경우 모집 경우를 삭제할 수 없습니다."),
    WORK_DATE_NOT_FOUND(HttpStatus.NOT_FOUND, "WORKDATE-001", "채용 공고의 날짜 정보가 존재하지 않습니다."),
    WORK_DATE_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, "WORKDATE-002", "요청한 날짜 중 모집 공고에 맞지 않는 날짜가 있습니다."),
    WORK_DATE_RECRUITMENT_FULL(HttpStatus.BAD_REQUEST, "WORKDATE-003", "이미 모집 인원이 충족되었습니다."),
    WORK_DATE_NOT_MATCH(HttpStatus.BAD_REQUEST, "WORKDATE-004", "공고에 해당하는 workdate의 id값이 아닙니다."),
    WORK_DATE_INVALID_RANGE(HttpStatus.BAD_REQUEST, "WORKDATE-005", "모집 공고 날짜가 프로젝트 기간에 포함되지 않습니다"),
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT-001", "프로젝트 정보가 없습니다."),
    PROJECT_DELETE_FAIL(HttpStatus.CONFLICT, "PROJECT-002", "프로젝트에 등록된 모집 공고가 있을 시 삭제할 수 없습니다."),

    /**
     * 근무 기록
     */
    HISTORY_NOT_FOUND_APPLY(HttpStatus.NOT_FOUND, "HISTORY-001", "일자리 공고에 신청한 내역이 없습니다."),
    HISTORY_ALREADY_EXIST(HttpStatus.CONFLICT, "HISTORY-002", "이미 동일한 출근, 결근 데이터가 있습니다."),
    HISTORY_UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "HISTORY-003", "출,퇴근 데이터 업데이트 중 에러가 발생했습니다."),

    /**
     * 이력서 (맞춤 정보)
     */
    RESUME_NOT_FOUND(HttpStatus.NOT_FOUND, "RESUME-001", "이력서 정보가 없습니다."),

    /**
     * 제안
     */
    OFFER_NOT_FOUND(HttpStatus.NOT_FOUND, "OFFER-001", "제안(여러 날짜 묶음) 내역이 없습니다."),
    OFFER_WORK_DATE_NOT_FOUND(HttpStatus.NOT_FOUND, "OFFER-002", "제안(개별 날짜) 내역이 없습니다."),
    OFFER_INVALID_DATE(HttpStatus.BAD_REQUEST, "OFFER-003", "제안을 처리할 수 있는 시점이 아닙니다."),
    OFFER_CANCEL_FAIL(HttpStatus.BAD_REQUEST, "OFFER-004", "노동자가 처리하지 않은 제안에 대해서만 취소할 수 있습니다."),

    /**
     * 최근 기록
     */
    SEARCH_LOG_NOT_EXIST(HttpStatus.NOT_FOUND, "SEARCHLOG-001", "검색 기록을 찾을 수 없습니다."),

    /**
     * s3
     */
    S3_NOT_FOUND_FILE_NAME(HttpStatus.NOT_FOUND, "S3-001", "s3 버킷 내 target 파일 명이 존재하지 않습니다."),
    S3_URL_INVALID(HttpStatus.BAD_REQUEST, "S3-002", "올바른 S3 URL 형식이 아닙니다: "),

    /**
     * validation
     */
    REQUEST_INVALID(HttpStatus.BAD_REQUEST, "VALID-001", "valid 옵션에 맞지 않는 형식입니다."),

    /**
     * 스케줄링
     */
    SCHEDULER_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SCHEDULER-001", "스케줄러 동작 중 에러가 발생했습니다."),

    /**
     * 동시성
     */
    CONCURRENCY_FAILURE(HttpStatus.CONFLICT, "CONCURRENCY-001", "동시성 문제가 발생하였습니다. 다시 시도해주세요."),

    /**
     * SMS 발송
     */
    SMS_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "SMS-001", "문자 발송 중 에러가 발생했습니다."),
    SMS_BAD_REQUEST(HttpStatus.BAD_REQUEST, "SMS-002", "잘못된 요청입니다."),
    SMS_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "SMS-003", "인증되지 않은 요청입니다."),
    SMS_FORBIDDEN(HttpStatus.FORBIDDEN, "SMS-004", "접근이 금지되었습니다."),
    SMS_NOT_FOUND(HttpStatus.NOT_FOUND, "SMS-005", "요청한 리소스를 찾을 수 없습니다."),
    SMS_TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "SMS-006", "요청이 너무 많습니다. 나중에 다시 시도하세요."),
    SMS_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SMS-007", "서버 내부 오류가 발생했습니다."),

    /**
     * 비자 사진
     */
    VISA_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "VISA-001", "비자 사진 정보가 없습니다."),

    /**
     * codef
     */
    CODEF_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "CODEF-001", "codef 관련 로직 수행 중 에러가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String errorMessage;
}

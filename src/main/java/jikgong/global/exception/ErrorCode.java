package jikgong.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보가 없습니다."),
    MEMBER_PHONE_EXIST(HttpStatus.CONFLICT, "이미 등록된 핸드폰 번호입니다."),
    MEMBER_INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다."),
    REQUEST_INVALID(HttpStatus.BAD_REQUEST, "valid 옵션에 맞지 않는 형식입니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 access token 입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 refresh token 입니다."),
    REFRESH_TOKEN_NOT_MATCH(HttpStatus.FORBIDDEN, "유효하지 않은 refresh token 입니다. 다시 로그인하세요."),
    FCM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FCM 알림 전송 중 에러가 발생했습니다."),
    FCM_FIREBASE_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보에 FCM 토큰이 없습니다."),
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "위치 정보가 없습니다."),
    WAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "임금 지급 내역 정보가 없습니다."),
    FILE_NOT_FOUND_EXTENSION(HttpStatus.BAD_REQUEST, "확장자를 찾을 수 없습니다."),
    FILE_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "지원하지 않는 확장자 입니다."),
    IMAGE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 처리 중 서버에서 에러가 발생했습니다."),
    CERTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "회원의 증명서 정보가 없습니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요 누른 정보가 없습니다."),
    LIKE_REQUEST_INVALID(HttpStatus.BAD_REQUEST, "좋아요는 기업이 노동자에게 누를 수 있습니다."),
    LIKE_ALREADY_EXIST(HttpStatus.CONFLICT, "해당 회원에겐 이미 좋아요를 눌렀습니다."),
    JOB_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "모집 공고 정보가 없습니다."),
    JOB_POST_EXPIRED(HttpStatus.BAD_REQUEST, "모집 기한이 지난 공고입니다."),
    RECRUITMENT_FULL(HttpStatus.BAD_REQUEST, "이미 모집 인원이 충족되었습니다."),
    APPLY_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 신청한 모집 공고 입니다."),
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "프로젝트 정보가 없습니다."),
    HISTORY_NOT_FOUND_APPLY(HttpStatus.NOT_FOUND, "일자리 공고에 신청한 내역이 없습니다."),
    HISTORY_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 동일한 출근, 결근 데이터가 있습니다."),
    WORK_DATE_NOT_FOUND(HttpStatus.NOT_FOUND, "날짜 엔티티가 존재하지 않습니다."),
    WORK_DATE_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 날짜 중 모집 공고에 맞지 않는 날짜가 있습니다."),
    APPLY_NOT_ACCEPTED(HttpStatus.BAD_REQUEST, "신청 내역이 아직 승인 되지 않았습니다."),
    APPLY_NEED_TO_PENDING(HttpStatus.BAD_REQUEST, "대기 중인 요청에 대해서만 처리할 수 있습니다."),
    APPLY_OVER_RECRUIT_NUM(HttpStatus.BAD_REQUEST, "모집 인원을 초과했습니다."),
    APPLY_ALREADY_ACCEPTED_IN_WORKDATE(HttpStatus.CONFLICT, "이미 승인된 지원 내역이 있습니다."),
    HISTORY_UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "출,퇴근 데이터 업데이트 중 에러가 발생했습니다."),
    SEARCH_LOG_NOT_EXIST(HttpStatus.NOT_FOUND, "Redis 에서 요청한 검색 기록을 찾을 수 없습니다."),
    HEAD_HUNTING_NOT_FOUND(HttpStatus.NOT_FOUND, "헤드 헌팅 등록한 정보가 없습니다."),
    EMPTY_WORK_DATE_LIST(HttpStatus.NOT_FOUND, "날짜 정보가 없습니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "알림 정보가 없습니다."),
    RESUME_NOT_FOUND(HttpStatus.NOT_FOUND, "이력서 정보가 없습니다."),
    WORK_DATE_NEED_TO_FUTURE(HttpStatus.BAD_REQUEST, "당일, 과거 날짜에 대한 일자리는 제안할 수 없습니다."),
    WORK_DATE_NOT_MATCH(HttpStatus.BAD_REQUEST, "요청한 workDate id 값이 jobPost와 연관되어있지 않습니다."),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청된 회원의 ROLE을 특정할 수 없습니다."),
    SCHEDULER_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "스케줄러 동작 중 에러가 발생했습니다."),
    MEMBER_LOGIN_ID_EXIST(HttpStatus.CONFLICT, "이미 등록된 id입니다."),
    OFFER_NOT_FOUND(HttpStatus.NOT_FOUND, "제안 내역이 없습니다."),
    OFFER_WORK_DATE_NOT_FOUND(HttpStatus.NOT_FOUND, "제안 내역이 없습니다."),
    OFFER_PROCESS_NEED_TO_ONE_DAY_AGO(HttpStatus.BAD_REQUEST, "제안 수락은 출역일 하루 전 날까지 가능합니다.");

    private final HttpStatus status;
    private final String errorMessage;

}

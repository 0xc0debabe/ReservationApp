package reservation.hmw.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {


    NOT_EXISTS_EMAIL(HttpStatus.BAD_REQUEST, "해당 이메일은 존재하지 않습니다."),
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "해당 유저는 존재하지 않습니다."),
    MISSING_STORENAME_OR_KEYWORD(HttpStatus.BAD_REQUEST, "매장명 또는 키워드 중 하나를 입력해야 합니다."),
    NOT_FOUND_STORE(HttpStatus.BAD_REQUEST, "해당 매장은 조회되지 않습니다."),
    NOT_COLLECT_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    PARTNER_ONLY_ACCESS(HttpStatus.BAD_REQUEST, "파트너 회원만 접근할 수 있습니다."),
    NOT_LOGGED_IN(HttpStatus.BAD_REQUEST, "로그인한 회원만 접근할 수 있습니다."),
    ALREADY_EXISTS_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다.");


    private final HttpStatus httpStatus;
    private final String  description;

}

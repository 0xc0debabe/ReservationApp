package reservation.hmw.exception;

import lombok.Getter;


/**
 * 사용자 정의 예외 클래스입니다.
 * 특정 오류 코드와 함께 예외를 발생시키기 위해 사용됩니다.
 */
@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

}
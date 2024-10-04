package reservation.hmw;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

/**
 * 유효성 검사 클래스입니다.
 */
public class Validation {

    /**
     * 유효성 검사 오류에 대한 응답을 생성하는 메서드입니다.
     *
     * @param bindingResult 유효성 검사 결과
     * @return 유효성 검사 오류 메시지를 포함한 응답
     */
    public static ResponseEntity<String> getErrorResponse(BindingResult bindingResult) {
        StringBuilder errorMessage = new StringBuilder("Validation errors :").append('\n');
        bindingResult.getFieldErrors().forEach(error -> {
            errorMessage.append(error.getField()).append(": ").append(error.getDefaultMessage()).append('\n');
        });
        return ResponseEntity.badRequest().body(errorMessage.toString());
    }

}

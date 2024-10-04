package reservation.hmw.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reservation.hmw.Validation;
import reservation.hmw.model.entity.dto.LoginForm;
import reservation.hmw.model.entity.dto.RegisterForm;
import reservation.hmw.model.entity.session.SessionConst;
import reservation.hmw.service.UserService;

/**
 * 사용자 관련 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    /**
     * 사용자 회원가입을 처리하는 메서드입니다.
     *
     * @param form 회원가입에 필요한 정보가 포함된 폼입니다.
     * @param bindingResult 유효성 검사 결과입니다.
     * @return 등록된 사용자 정보를 포함한 HTTP 200 OK 응답을 반환합니다.
     */
    @PostMapping("register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterForm.Request form,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return Validation.getErrorResponse(bindingResult);
        }

        return ResponseEntity.ok(userService.registerUser(form));
    }

    /**
     * 사용자 로그인을 처리하는 메서드입니다.
     *
     * @param form 로그인에 필요한 정보가 포함된 폼입니다.
     * @param bindingResult 유효성 검사 결과입니다.
     * @param request 세션 확인을 위한 요청입니다.
     * @return 로그인 성공 시 사용자 정보를 포함한 HTTP 200 OK 응답을 반환합니다.
     */
    @PostMapping("login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginForm.Request form,
                                          BindingResult bindingResult,
                                          HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return Validation.getErrorResponse(bindingResult);
        }

        LoginForm.Response loginResponse = userService.loginUser(form);
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_USER, loginResponse.getId());

        return ResponseEntity.ok(loginResponse);
    }

    /**
     * 사용자 로그아웃을 처리하는 메서드입니다.
     *
     * @param request 세션 확인을 위한 요청입니다.
     * @return 로그아웃 처리 결과를 포함한 HTTP 200 OK 응답을 반환합니다.
     */
    @PostMapping("logout")
    public ResponseEntity<?> logoutPartner(HttpServletRequest request) {
        return ResponseEntity.ok(userService.logout(request));
    }

}

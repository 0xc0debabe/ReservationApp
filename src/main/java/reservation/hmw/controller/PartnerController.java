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
import reservation.hmw.service.PartnerService;

/**
 * 파트너 회원에 관한 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("partner")
public class PartnerController {

    private final PartnerService partnerService;

    /**
     * 파트너 회원가입을 처리하는 메서드입니다.
     *
     * @param form 회원가입에 필요한 정보가 포함된 폼입니다.
     * @param bindingResult 유효성 검사 결과입니다.
     * @return 등록된 파트너 정보를 포함한 HTTP 200 OK 응답을 반환합니다.
     */
    @PostMapping("register")
    public ResponseEntity<?> registerPartner(
            @Valid @RequestBody RegisterForm.Request form,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return Validation.getErrorResponse(bindingResult);
        }

        return ResponseEntity.ok(partnerService.registerPartner(form));
    }

    /**
     * 파트너 회원 로그인을 처리하는 메서드입니다.
     *
     * @param form 로그인에 필요한 정보가 포함된 폼입니다.
     * @param bindingResult 유효성 검사 결과입니다.
     * @param request 세션 정보를 위한 요청입니다.
     * @return 로그인 성공 시 파트너 정보를 포함한 HTTP 200 OK 응답을 반환합니다.
     */
    @PostMapping("login")
    public ResponseEntity<?> loginPartner(@Valid @RequestBody LoginForm.Request form,
                                          BindingResult bindingResult,
                                          HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return Validation.getErrorResponse(bindingResult);
        }

        LoginForm.Response loginPartnerResponse = partnerService.loginPartner(form);
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_PARTNER, loginPartnerResponse.getId());

        return ResponseEntity.ok(loginPartnerResponse);
    }

    /**
     * 파트너 로그아웃을 처리하는 메서드입니다.
     *
     * @param request 세션 정보를 위한 요청입니다.
     * @return 로그아웃 처리 결과를 포함한 HTTP 200 OK 응답을 반환합니다.
     */
    @PostMapping("logout")
    public ResponseEntity<?> logoutPartner(HttpServletRequest request) {
        return ResponseEntity.ok(partnerService.logout(request));
    }

}

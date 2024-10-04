package reservation.hmw.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reservation.hmw.exception.CustomException;
import reservation.hmw.exception.ErrorCode;
import reservation.hmw.model.entity.Partner;
import reservation.hmw.model.entity.dto.LoginForm;
import reservation.hmw.model.entity.dto.LogoutForm;
import reservation.hmw.model.entity.dto.RegisterForm;
import reservation.hmw.model.entity.session.SessionConst;
import reservation.hmw.repository.PartnerRepository;

/**
 * 파트너 회원을 위한 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;

    /**
     * 파트너 회원가입을 처리하는 메서드입니다.
     *
     * @param form 회원가입 정보가 포함된 요청 폼
     * @return 등록된 파트너 정보를 포함한 응답 폼
     */
    public RegisterForm.Response registerPartner(RegisterForm.Request form) {
        if (partnerRepository.existsByEmail(form.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_EMAIL);
        }

        Partner partner = partnerRepository.save(form.toPartnerEntity());

        return RegisterForm.Response.builder()
                .email(partner.getEmail())
                .name(partner.getName())
                .build();
    }

    /**
     * 파트너 로그인을 처리하는 메서드입니다.
     *
     * @param form 로그인 정보가 포함된 요청 폼
     * @return 로그인한 파트너 정보를 포함한 응답 폼
     */
    public LoginForm.Response loginPartner(LoginForm.Request form) {
        Partner partner = partnerRepository.findByEmail(form.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_EMAIL));

        if (!partner.getPassword().equals(form.getPassword())) {
            throw new CustomException(ErrorCode.NOT_COLLECT_PASSWORD);
        }

        return LoginForm.Response.builder()
                .id(partner.getId())
                .email(partner.getEmail())
                .name(partner.getName())
                .build();
    }

    /**
     * 파트너 로그아웃을 처리하는 메서드입니다.
     *
     * @param request 세션 정보를 포함한 HTTP 요청
     * @return 로그아웃한 파트너의 ID를 포함한 응답 폼
     */
    public LogoutForm logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long partnerId = null;

        if (session != null) {
            partnerId = (Long) session.getAttribute(SessionConst.LOGIN_PARTNER);
            session.invalidate();
        }

        return new LogoutForm(partnerId);
    }

}

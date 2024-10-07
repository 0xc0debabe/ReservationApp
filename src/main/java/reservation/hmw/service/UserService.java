package reservation.hmw.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import reservation.hmw.exception.CustomException;
import reservation.hmw.exception.ErrorCode;
import reservation.hmw.model.entity.User;
import reservation.hmw.model.entity.dto.LoginForm;
import reservation.hmw.model.entity.dto.LogoutForm;
import reservation.hmw.model.entity.dto.RegisterForm;
import reservation.hmw.model.entity.session.SessionConst;
import reservation.hmw.repository.UserRepository;

/**
 * 유저에 대한 서비스입니다.
 */
@Service
@RequiredArgsConstructor
@RequestMapping("user")
public class UserService {

    private final UserRepository userRepository;

    /**
     * 사용자 등록 메서드입니다.
     *
     * @param form 사용자 등록 정보가 포함된 요청 폼
     * @return 등록된 사용자 정보
     */
    @Transactional
    public RegisterForm.Response registerUser(RegisterForm.Request form) {
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_EMAIL);
        }

        User user = userRepository.save(form.toUserEntity());

        return RegisterForm.Response.builder()
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    /**
     * 사용자 로그인 메서드입니다.
     *
     * @param form 로그인 요청 정보가 포함된 폼
     * @return 로그인한 사용자 정보
     */
    public LoginForm.Response loginUser(LoginForm.Request form) {
        User user = userRepository.findByEmail(form.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_EMAIL));

        if (!user.getPassword().equals(form.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        return LoginForm.Response.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    /**
     * 사용자 로그아웃 메서드입니다.
     *
     * @param request 세션 정보를 포함한 요청
     * @return 로그아웃한 사용자 정보
     */
    public LogoutForm logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long partnerId = null;

        if (session != null) {
            partnerId = (Long) session.getAttribute(SessionConst.LOGIN_USER);
            session.invalidate();
        }

        return new LogoutForm(partnerId);
    }

}

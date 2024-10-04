package reservation.hmw.model.entity.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import reservation.hmw.exception.CustomException;
import reservation.hmw.exception.ErrorCode;

public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();

        if (session == null ||
                (session.getAttribute(SessionConst.LOGIN_USER) == null &&
                        session.getAttribute(SessionConst.LOGIN_PARTNER) == null)) {

            throw new CustomException(ErrorCode.NOT_LOGGED_IN);
        }

        return true;
    }

}

package reservation.hmw.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import reservation.hmw.exception.CustomException;
import reservation.hmw.exception.ErrorCode;
import reservation.hmw.model.entity.User;
import reservation.hmw.model.entity.dto.LoginForm;
import reservation.hmw.model.entity.dto.LogoutForm;
import reservation.hmw.model.entity.dto.RegisterForm;
import reservation.hmw.model.entity.session.SessionConst;
import reservation.hmw.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpSession session;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_success() {
        //given
        RegisterForm.Request request = RegisterForm.Request.builder()
                .name("test")
                .email("test@naver.com")
                .password("1234")
                .phone("010")
                .build();

        given(userRepository.existsByEmail(any()))
                .willReturn(false);

        given(userRepository.save(any()))
                .willReturn(request.toUserEntity());

        //when
        RegisterForm.Response response = userService.registerUser(RegisterForm.Request.builder()
                .email("asd")
                .name("asdf")
                .password("2f")
                .build());

        //then
        Assertions.assertThat(response.getEmail()).isEqualTo("test@naver.com");
        Assertions.assertThat(response.getName()).isEqualTo("test");
     }

    @Test
    void registerUser_ALREADY_EXISTS_EMAIL() {
        //given
        RegisterForm.Request request = RegisterForm.Request.builder()
                .name("test")
                .email("test@naver.com")
                .password("1234")
                .build();

        given(userRepository.existsByEmail(any()))
                .willReturn(true);

        //when
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.registerUser(request));

        //then
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ALREADY_EXISTS_EMAIL);
    }

    @Test
    void loginUser_success() {
        //given
        User user = User.builder()
                .id(2L)
                .name("test")
                .email("test@naver.com")
                .password("1234")
                .build();

        LoginForm.Request request = LoginForm.Request.builder()
                .email("a@naver.com")
                .password("1234")
                .build();

        given(userRepository.findByEmail(anyString()))
                .willReturn(Optional.of(user));

        //when
        LoginForm.Response actual = userService.loginUser(request);

        //then
        Assertions.assertThat(actual.getId()).isEqualTo(2L);
        Assertions.assertThat(actual.getName()).isEqualTo("test");
        Assertions.assertThat(actual.getEmail()).isEqualTo("test@naver.com");
     }

     @Test
     void loginUser_NOT_EXISTS_EMAIL() {
         //given
         LoginForm.Request request = LoginForm.Request.builder()
                 .email("a@naver.com")
                 .password("1234")
                 .build();

         given(userRepository.findByEmail(anyString()))
                 .willReturn(Optional.empty());

         //when
         CustomException exception = assertThrows(CustomException.class,
                 () -> userService.loginUser(request));

         //then
         Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_EXISTS_EMAIL);
      }

    @Test
    void loginUser_NOT_COLLECT_PASSWORD() {
        //given
        User user = User.builder()
                .id(2L)
                .name("test")
                .email("test@naver.com")
                .password("1234")
                .build();

        LoginForm.Request request = LoginForm.Request.builder()
                .email("a@naver.com")
                .password("123")
                .build();

        given(userRepository.findByEmail(anyString()))
                .willReturn(Optional.of(user));

        //when
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.loginUser(request));

        //then
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD);
    }

    @Test
    void logout_success() {
        //given
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_USER, 5L);
        given(request.getSession(false)).willReturn(mockHttpSession);

        //when
        LogoutForm logout = userService.logout(request);

        //then
        Assertions.assertThat(logout.getId()).isEqualTo(5L);
     }

}
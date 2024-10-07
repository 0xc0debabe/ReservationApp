package reservation.hmw.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reservation.hmw.exception.CustomException;
import reservation.hmw.exception.ErrorCode;
import reservation.hmw.model.entity.Partner;
import reservation.hmw.model.entity.dto.LoginForm;
import reservation.hmw.model.entity.dto.LogoutForm;
import reservation.hmw.model.entity.dto.RegisterForm;
import reservation.hmw.model.entity.session.SessionConst;
import reservation.hmw.repository.PartnerRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PartnerServiceTest {

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @InjectMocks
    private PartnerService partnerService;

    RegisterForm.Request successPartner;

    @BeforeEach
    void setup() {
        successPartner = RegisterForm.Request.builder()
                .email("test@naver.com")
                .name("testPartner")
                .password("1234")
                .phone("0103")
                .build();

    }

    @Test
    void success_register() {
        //given
        given(partnerRepository.existsByEmail(anyString()))
                .willReturn(false);

        given(partnerRepository.save(any()))
                .willReturn(successPartner.toPartnerEntity());

        //when
        RegisterForm.Response registerPartner =
                partnerService.registerPartner(RegisterForm.Request.builder()
                        .name("who")
                        .email("adsf@naver.com")
                        .password("!234")
                        .phone("010")
                        .build()
                );

        //then
        Assertions.assertThat(registerPartner.getEmail()).isEqualTo("test@naver.com");
        Assertions.assertThat(registerPartner.getName()).isEqualTo("testPartner");
     }

     @Test
     void validation_register_existEmail() {
         //given
         given(partnerRepository.existsByEmail(anyString()))
                 .willReturn(true);

         //when
         CustomException customException = assertThrows(CustomException.class,
                 () -> partnerService.registerPartner(successPartner));

         //then
         Assertions.assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.ALREADY_EXISTS_EMAIL);
     }

     @Test
     void loginPartner_success() {
         //given
         Partner partner = Partner.builder()
                 .id(3L)
                 .name("홍길동")
                 .email("test@naver.com")
                 .password("test")
                 .build();

         given(partnerRepository.findByEmail(anyString()))
                 .willReturn(Optional.of(partner));

         //when
         LoginForm.Response response = partnerService.loginPartner(LoginForm.Request.builder()
                 .email("t@naver.com")
                 .password("test")
                 .build());

         //then
         Assertions.assertThat(response.getId()).isEqualTo(3L);
         Assertions.assertThat(response.getName()).isEqualTo("홍길동");
         Assertions.assertThat(response.getEmail()).isEqualTo("test@naver.com");
      }

      @Test
      void loginPartner_NOT_EXISTS_EMAIL() {
          //given
          given(partnerRepository.findByEmail(any()))
                  .willReturn(Optional.empty());

          //when
          CustomException customException = assertThrows(CustomException.class,
                  () -> partnerService.loginPartner(
                          LoginForm.Request.builder()
                                  .email("t@naver.com")
                                  .password("test")
                                  .build()));

          //then
          Assertions.assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.NOT_EXISTS_EMAIL);
       }

    @Test
    void loginPartner_NOT_COLLECT_PASSWORD() {
        //given
        Partner partner = Partner.builder()
                .id(3L)
                .name("홍길동")
                .email("test@naver.com")
                .password("test")
                .build();

        given(partnerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(partner));

        //when
        CustomException exception = assertThrows(CustomException.class,
                () -> partnerService.loginPartner(LoginForm.Request.builder()
                        .email("t@naver.com")
                        .password("t")
                        .build()));

        //then
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD);
    }

    @Test
    void logout_success() {
        //given
        given(request.getSession(false)).willReturn(session);
        given(session.getAttribute(SessionConst.LOGIN_PARTNER))
                .willReturn(3L);

        //when
        LogoutForm logout = partnerService.logout(request);

        //then
        Assertions.assertThat(logout.getId()).isEqualTo(3L);
        verify(session, times(1)).invalidate();
     }

}
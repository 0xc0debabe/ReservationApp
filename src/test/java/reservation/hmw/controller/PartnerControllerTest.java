package reservation.hmw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import reservation.hmw.model.entity.dto.LoginForm;
import reservation.hmw.model.entity.dto.RegisterForm;
import reservation.hmw.model.entity.session.SessionConst;
import reservation.hmw.service.PartnerService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PartnerController.class)
class PartnerControllerTest {

    @MockBean
    private PartnerService partnerService;

    @MockBean
    private StoreController storeController;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    RegisterForm.Request requestForm;
    RegisterForm.Response responseForm;

    @BeforeEach
    public void init() {
        requestForm = RegisterForm.Request
                .builder()
                .email("test@naver.com")
                .name("testPartner")
                .password("1234")
                .build();

        responseForm = RegisterForm.Response
                .builder()
                .name("testName")
                .email("test@gmail.com")
                .build();

    }

    @Test
    void success_register() throws Exception {
        //given
        given(partnerService.registerPartner(any()))
                .willReturn(responseForm);

        //when //then

        mockMvc.perform(post("/partner/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                RegisterForm.Request
                                        .builder()
                                        .email("asdf@naver.com")
                                        .name("test")
                                        .password("1234")
                                        .build()
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.name").value("testName"));
     }

     @Test
     void register_validation() throws Exception {
         //given
         RegisterForm.Request validUser = RegisterForm.Request
                 .builder()
                 .name("")
                 .email("")
                 .password("")
                 .build();

         //when
         //then
         mockMvc.perform(post("/partner/register")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(validUser)))
                 .andExpect(status().isBadRequest())
                 .andExpect(result -> {
                     String content = result.getResponse().getContentAsString();
                     System.out.println(content);
                 });
      }

      @Test
      void loginPartner_success() throws Exception{
          //given
          LoginForm.Response formResponse = LoginForm.Response.builder()
                  .id(2L)
                  .name("홍길동")
                  .email("test@naver.com")
                  .build();

          given(partnerService.loginPartner(any()))
                  .willReturn(formResponse);

          //when //then

          MvcResult mvcResult = mockMvc.perform(post("/partner/login")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(objectMapper.writeValueAsString(
                                  new LoginForm.Request("asdf@naver.com", "aa")
                          )))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$.email").value("test@naver.com"))
                  .andExpect(jsonPath("$.name").value("홍길동"))
                  .andExpect(jsonPath("$.id").value(2L))
                  .andReturn();

          HttpSession session = mvcResult.getRequest().getSession(false);
          Assertions.assertThat(session.getAttribute(SessionConst.LOGIN_PARTNER)).isEqualTo(2L);
      }

    @Test
    void loginPartner_validation() throws Exception {
        //given
        LoginForm.Request loginForm = LoginForm.Request.builder()
                .email("asdf")
                .password("")
                .build();

        //  when //then
        mockMvc.perform(post("/partner/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    System.out.println(content);
                });
    }

    @Test
    void logout_success() throws Exception {
        // given

        // when // then
        mockMvc.perform(post("/partner/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
package reservation.hmw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import reservation.hmw.model.entity.dto.LoginForm;
import reservation.hmw.model.entity.dto.RegisterForm;
import reservation.hmw.model.entity.session.SessionConst;
import reservation.hmw.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Mock
    private HttpSession session;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_success() throws Exception {
        //given
        RegisterForm.Request request = RegisterForm.Request.builder()
                .email("test@naver.com")
                .name("test")
                .password("1234")
                .phone("010")
                .build();

        RegisterForm.Response response = RegisterForm.Response.builder()
                .email("test1@naver.com")
                .name("test1")
                .build();

        given(userService.registerUser(any()))
                .willReturn(response);

        //when //then
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test1@naver.com"))
                .andExpect(jsonPath("$.name").value("test1"));
     }

     @Test
     void login_success() throws Exception {
         //given
         LoginForm.Request request = LoginForm.Request.builder()
                 .email("test@naver.com")
                 .password("1234")
                 .build();

         LoginForm.Response response = LoginForm.Response.builder()
                 .id(5L)
                 .name("test1")
                 .email("test2@naver.com")
                 .build();

         MockHttpSession mockHttpSession = new MockHttpSession();
         mockHttpSession.setAttribute(SessionConst.LOGIN_USER, 5L);

         given(userService.loginUser(any()))
                 .willReturn(response);


         //when //then
         mockMvc.perform(post("/user/login")
                         .session(mockHttpSession)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.id").value(5L))
                 .andExpect(jsonPath("$.name").value("test1"))
                 .andExpect(jsonPath("$.email").value("test2@naver.com"));
      }

      @Test
      void logout_success() throws Exception{
          // given
          // when // then
          mockMvc.perform(post("/user/logout"))
                  .andExpect(status().isOk());
       }

}
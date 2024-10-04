package reservation.hmw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reservation.hmw.model.entity.dto.ReservationDto;
import reservation.hmw.model.entity.session.SessionConst;
import reservation.hmw.service.ReservationService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createReservation_success() throws Exception{
        //given
        MockMvc mvc = MockMvcBuilders
                .standaloneSetup(new ReservationController(reservationService))
                .build();

        LocalDateTime now = LocalDateTime.now();
        ReservationDto.Response response = ReservationDto.Response
                .builder()
                .userName("kk")
                .storeName("ch")
                .reservationTime(now)
                .build();

        ReservationDto.Request request = ReservationDto.Request.builder()
                .userId(1L)
                .storeId(1L)
                .reservationTime(LocalDateTime.now().plusDays(1))
                .build();


        given(reservationService.createReservation(any()))
                .willReturn(response);

        //when //then
        mvc.perform(post("/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.userName").value("kk"))
                .andExpect(jsonPath("$.storeName").value("ch"));
     }

    @Test
    void userCanAccess() throws Exception{
        //given
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_USER, 1L);

        LocalDateTime now = LocalDateTime.now();
        ReservationDto.Response response = ReservationDto.Response
                .builder()
                .userName("kk")
                .storeName("ch")
                .reservationTime(now)
                .build();

        ReservationDto.Request request = ReservationDto.Request.builder()
                .userId(1L)
                .storeId(1L)
                .reservationTime(LocalDateTime.now().plusDays(1))
                .build();


        given(reservationService.createReservation(any()))
                .willReturn(response);

        //when //then
        mockMvc.perform(post("/reservation")
                        .session(mockHttpSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("kk"))
                .andExpect(jsonPath("$.storeName").value("ch"));
    }

    @Test
    void partnerCanAccess() throws Exception{
        //given
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_PARTNER, 1L);

        LocalDateTime now = LocalDateTime.now();
        ReservationDto.Response response = ReservationDto.Response
                .builder()
                .userName("kk")
                .storeName("ch")
                .reservationTime(now)
                .build();

        ReservationDto.Request request = ReservationDto.Request.builder()
                .userId(1L)
                .storeId(1L)
                .reservationTime(LocalDateTime.now().plusDays(1))
                .build();


        given(reservationService.createReservation(any()))
                .willReturn(response);

        //when //then
        mockMvc.perform(post("/reservation")
                        .session(mockHttpSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("kk"))
                .andExpect(jsonPath("$.storeName").value("ch"));
    }

    // 테스트 실패
    @Test
    void NOT_LOGGED_IN_ACCESS() throws Exception{
        //given
        MockHttpSession mockHttpSession = new MockHttpSession();

        LocalDateTime now = LocalDateTime.now();
        ReservationDto.Response response = ReservationDto.Response
                .builder()
                .userName("kk")
                .storeName("ch")
                .reservationTime(now)
                .build();

        ReservationDto.Request request = ReservationDto.Request.builder()
                .userId(1L)
                .storeId(1L)
                .reservationTime(LocalDateTime.now().plusDays(1))
                .build();


        given(reservationService.createReservation(any()))
                .willReturn(response);

        //when //then
        MvcResult mvcResult = mockMvc.perform(post("/reservation")
                        .session(mockHttpSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

}
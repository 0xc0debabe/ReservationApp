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
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import reservation.hmw.model.entity.dto.ConfirmReservationDto;
import reservation.hmw.model.entity.dto.ReservationDto;
import reservation.hmw.model.entity.session.SessionConst;
import reservation.hmw.service.ReservationService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        mockMvc.perform(post("/reservation")
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }


    @Test
    void confirmReservation_success() throws Exception {
        //given
        ConfirmReservationDto.Request request = ConfirmReservationDto.Request.builder()
                .phone("010-1234-5678")
                .build();

        LocalDateTime time = LocalDateTime.now();
        ConfirmReservationDto.Response response = ConfirmReservationDto.Response.builder()
                .reservationTime(time)
                .storeName("storeName")
                .userName("kim")
                .build();

        given(reservationService.confirmReservation(any()))
                .willReturn(response);

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_PARTNER, 1L);

        //when //then
        mockMvc.perform(post("/reservation/confirm")
                        .session(mockHttpSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeName").value("storeName"))
                .andExpect(jsonPath("$.userName").value("kim"));

    }

    @Test
    void approveReservation_success() throws Exception {
        //given
        MockMvc mock = MockMvcBuilders.standaloneSetup(new ReservationController(reservationService)).build();

        Long reservationId = 1L;

        //when

        //then
        mock.perform(put("/reservation/approve/{reservationId}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("APPROVED"));
    }

    @Test
    void rejectReservation() throws Exception{
        //given
        MockMvc mock = MockMvcBuilders.standaloneSetup(new ReservationController(reservationService)).build();

        Long reservationId = 1L;
        //when

        //then
        mock.perform(put("/reservation/reject/{reservationId}", reservationId))
                .andExpect(status().isOk())
                .andExpect(content().string("REJECTED"));
     }
}
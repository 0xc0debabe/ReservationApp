package reservation.hmw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import reservation.hmw.model.entity.dto.ReviewForm;
import reservation.hmw.model.entity.session.SessionConst;
import reservation.hmw.service.ReviewService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @InjectMocks
    private ReviewController reviewController;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void createReview_success() throws Exception {
        // given
        ReviewForm.Request formRequest = new ReviewForm.Request();
        formRequest.setStoreId(1L);
        formRequest.setRating(5);
        formRequest.setContent("Excellent!");


        ReviewForm.Response response = ReviewForm.Response.builder()
                .content("asdf")
                .storeName("ss")
                .userName("aa")
                .rating(5)
                .build();


        when(reviewService.createReview(any(), anyLong()))
                .thenReturn(response);


        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_USER, 2L);

        // when & then
        mockMvc.perform(post("/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(formRequest))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeName").value("ss"))
                .andExpect(jsonPath("$.content").value("asdf"));
    }

    @Test
    @DisplayName("리뷰 조회 성공 테스트")
    void getReview_success() throws Exception {
        // given
        Long reviewId = 1L;
        ReviewForm.Response reviewResponse = new ReviewForm.Response();
        reviewResponse.setContent("Great place!");

        when(reviewService.getReview(reviewId)).thenReturn(reviewResponse);

        // when & then
        mockMvc.perform(get("/review/{reviewId}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Great place!"));
    }

    @Test
    void updateReview_success() throws Exception {
        // given
        Long reviewId = 1L;
        ReviewForm.Request formRequest = new ReviewForm.Request();
        formRequest.setRating(4);
        formRequest.setContent("Good!");
        ReviewForm.Response response = ReviewForm.Response.builder()
                .content("asdf")
                .storeName("ss")
                .userName("aa")
                .rating(5)
                .build();

        when(reviewService.updateReview(any(ReviewForm.Request.class), anyLong(), anyLong())).thenReturn(response);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_USER, 2L);


        // when & then
        mockMvc.perform(put("/review/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(formRequest))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.content").value("asdf"));
    }

    @Test
    @DisplayName("리뷰 삭제 성공 테스트")
    void deleteReview_success() throws Exception {
        // given
        Long reviewId = 1L;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute(SessionConst.LOGIN_USER, 2L);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_USER, 2L);

        // when & then
        mockMvc.perform(delete("/review/{reviewId}", reviewId)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("DELETE"));

        verify(reviewService, times(1)).deleteReview(reviewId, 2L, null);
    }

    @Test
    void deleteReview_unauthorized() throws Exception {
        // given
        Long reviewId = 1L;
        MockHttpServletRequest request = new MockHttpServletRequest(); // 세션 설정 안 함

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_USER, 2L);


        // when & then
        mockMvc.perform(delete("/review/{reviewId}", reviewId)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    Assertions.assertThat(content).contains("DELETE");
                });
    }
}
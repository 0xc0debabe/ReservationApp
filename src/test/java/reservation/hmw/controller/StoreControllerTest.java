package reservation.hmw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import reservation.hmw.exception.CustomException;
import reservation.hmw.exception.ErrorCode;
import reservation.hmw.model.entity.Review;
import reservation.hmw.model.entity.Store;
import reservation.hmw.model.entity.User;
import reservation.hmw.model.entity.dto.StoreDetailDto;
import reservation.hmw.model.entity.dto.StoreInfoDto;
import reservation.hmw.model.entity.dto.StoreRegisterForm;
import reservation.hmw.model.entity.session.PartnerCheckInterceptor;
import reservation.hmw.model.entity.session.SessionConst;
import reservation.hmw.service.StoreService;

import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StoreController.class)
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PartnerCheckInterceptor interceptor;

    @MockBean
    private StoreService storeService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;



    private MockHttpSession mockHttpSession;

    @BeforeEach
    public void init() {
        mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionConst.LOGIN_PARTNER, 3L);
    }

    @Test
    void store_canAccess() throws Exception {
        // given
        given(request.getSession(false)).willReturn(mockHttpSession);

        // when // then
        MvcResult mvcResult = mockMvc.perform(post("/store/somePoint")
                        .session(mockHttpSession)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertThat(mvcResult.getRequest().getSession().getAttribute(SessionConst.LOGIN_PARTNER))
                .isEqualTo(3L);
    }

    @Test
    void store_register_preHandle_throwsException() throws Exception {
        // given
        MockHttpSession mockHttpSession1 = new MockHttpSession();
        mockHttpSession1.setAttribute(SessionConst.LOGIN_USER, 1L);

        // Interceptor의 preHandle에서 예외 발생 설정
        given(interceptor.preHandle(any(HttpServletRequest.class), any(HttpServletResponse.class), any(Object.class)))
                .willThrow(new CustomException(ErrorCode.PARTNER_ACCESS_ONLY));

        StoreRegisterForm form = StoreRegisterForm.builder()
                .storeName("Test Store")
                .location("Test Location")
                .storeDescription("This is a test store")
                .keyword("test")
                .build();

        // when // then
        mockMvc.perform(post("/store/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockHttpSession1)
                        .content(new ObjectMapper().writeValueAsString(form)))
                .andExpect(status().isBadRequest()) // 예외 발생 시 400 상태 코드 확인
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    Assertions.assertThat(content).isEqualTo("파트너 회원만 접근할 수 있습니다.");
                    System.out.println(content);
                });
    }

    @Test
    void register_success() throws Exception {
        //given
        StoreRegisterForm form = StoreRegisterForm.builder()
                .storeName("가게이름")
                .storeDescription("가게설명")
                .location("가게위치")
                .keyword("keyword")
                .build();

        Store store = Store.builder()
                .storeName(form.getStoreName())
                .storeDescription(form.getStoreDescription())
                .location(form.getLocation())
                .keyword(form.getKeyword())
                .build();

        given(storeService.registerStore(any(), anyLong()))
                .willReturn(store);

        //when //then
        mockMvc.perform(post("/store/register")
                        .session(mockHttpSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new StoreRegisterForm("a", "b", "c", "d")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeName").value("가게이름"))
                .andExpect(jsonPath("$.location").value("가게위치"))
                .andExpect(jsonPath("$.keyword").value("keyword"))
                .andExpect(jsonPath("$.storeDescription").value("가게설명"));
     }

     @Test
     void register_VALID() throws Exception{
         //given
         StoreRegisterForm form = StoreRegisterForm.builder()
                 .storeName(null)
                 .storeDescription(null)
                 .location(null)
                 .build();

         //when //then
         mockMvc.perform(post("/store/register")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(form))
                         .session(mockHttpSession))
                 .andExpect(status().isBadRequest())
                 .andExpect(result -> {
                     String content = result.getResponse().getContentAsString();
                     System.out.println(content);
                 });
      }

    @Test
    void searchStoreByKeyword_success() throws Exception {
        // given
        List<StoreInfoDto> storeInfoDtoList = Arrays.asList(
                StoreInfoDto.builder()
                        .storeName("a")
                        .keyword("cafe")
                        .location("aaaa")
                        .build(),

                StoreInfoDto.builder()
                        .storeName("b")
                        .keyword("cafe")
                        .location("bbbb")
                        .build()
        );

        given(storeService.searchStoreByKeyword(anyString()))
                .willReturn(storeInfoDtoList);
        MockMvc testMock = MockMvcBuilders
                .standaloneSetup(new StoreController(storeService))
                .build();

        // when // then
        testMock.perform(get("/store/search?keyword=cafe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].storeName").value("a"))
                .andExpect(jsonPath("$[0].keyword").value("cafe"))
                .andExpect(jsonPath("$[0].location").value("aaaa"))
                .andExpect(jsonPath("$[1].storeName").value("b"))
                .andExpect(jsonPath("$[1].keyword").value("cafe"))
                .andExpect(jsonPath("$[1].location").value("bbbb"));

    }

    @Test
    void searchStoreByStoreName_success() throws Exception {
        // given
        StoreInfoDto storeInfoDto = StoreInfoDto.builder()
                .storeName("a")
                .keyword("cafe")
                .location("aaaa")
                .build();

        given(storeService.searchStoreByName(anyString()))
                .willReturn(storeInfoDto);
        MockMvc testMock = MockMvcBuilders
                .standaloneSetup(new StoreController(storeService))
                .build();

        // when // then
        testMock.perform(get("/store/search?storeName=a")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.storeName").value("a"))
                .andExpect(jsonPath("$.keyword").value("cafe"))
                .andExpect(jsonPath("$.location").value("aaaa"));
    }

    @Test
    void searchStore_MISSING() throws Exception {
        // given
        MockMvc testMock = MockMvcBuilders
                .standaloneSetup(new StoreController(storeService))
                .build();

        // when // then
        testMock.perform(get("/store/search?")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> System.out.println(result.getResponse()));
    }

    @Test
    void detailStore_success() throws Exception{
        //given
        MockMvc testMock = MockMvcBuilders
                .standaloneSetup(new StoreController(storeService))
                .build();

        Store store = Store.builder()
                .location("loc")
                .storeName("storeName")
                .storeDescription("des")
                .build();

        User user = User.builder()
                .name("name")
                .build();

        List<Review> reviewList = Arrays.asList(
                Review.builder()
                        .store(store)
                        .user(user)
                        .rating(5)
                        .id(1L)
                        .build()
        );

        given(storeService.detailStore(anyLong()))
                .willReturn(new StoreDetailDto("desc", reviewList));

        //when //then
        testMock.perform(get("/store/detail/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeDescription").value("desc"));
     }

    @Test
    void updateStore_success() throws Exception {
        // given
        Store store = new Store();
        store.setStoreName("Updated Store Name");
        store.setStoreDescription("Updated Description");
        store.setLocation("Updated Location");
        store.setKeyword("Updated Keyword");

        StoreRegisterForm form = new StoreRegisterForm();
        form.setStoreName("Updated Store Name");
        form.setStoreDescription("Updated Description");
        form.setLocation("Updated Location");
        form.setKeyword("Updated Keyword");

        mockMvc = MockMvcBuilders.standaloneSetup(new StoreController(storeService)).build();
        given(storeService.updateStore(anyLong(), any(), anyLong()))
                .willReturn(store);

        Long storeId = 1L;

        // when // then
        mockMvc.perform(put("/store/update/{storeId}", storeId)
                        .session(mockHttpSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk());

    }

    @Test
    void deleteStore_success() throws Exception {
        Store store = new Store();
        store.setStoreName("Updated Store Name");
        store.setStoreDescription("Updated Description");
        store.setLocation("Updated Location");
        store.setKeyword("Updated Keyword");

        StoreRegisterForm form = new StoreRegisterForm();
        form.setStoreName("Updated Store Name");
        form.setStoreDescription("Updated Description");
        form.setLocation("Updated Location");
        form.setKeyword("Updated Keyword");

        mockMvc = MockMvcBuilders.standaloneSetup(new StoreController(storeService)).build();
        given(storeService.updateStore(anyLong(), any(), anyLong()))
                .willReturn(store);

        Long storeId = 1L;

        // when // then
        mockMvc.perform(delete("/store/delete/{storeId}", storeId)
                        .session(mockHttpSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk());
    }

}
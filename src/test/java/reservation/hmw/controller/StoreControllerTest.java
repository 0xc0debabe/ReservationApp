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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import reservation.hmw.model.entity.Store;
import reservation.hmw.model.entity.dto.StoreDetailDto;
import reservation.hmw.model.entity.dto.StoreInfoDto;
import reservation.hmw.model.entity.dto.StoreRegisterForm;
import reservation.hmw.model.entity.session.SessionConst;
import reservation.hmw.service.StoreService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoreController.class)
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StoreService storeService;

    @Mock
    private HttpServletRequest request;

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

    /**
     * 성공 못시켰음
     * 이후 session.getAttribute(SessionConst.LOGIN_PARTNER) == null)도 확인할 것
     * @throws Exception
     */
    @Test
    void store_canNotAccess1() throws Exception {
        //given

        //when //then
        mockMvc.perform(post("/store/somePoint")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(
                        result -> {
                            String content = result.getResponse().getContentAsString();
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

    /**
     * 성공 못시켰음
     * @throws Exception
     */
      @Test
      void register_validation2() throws Exception{
          //given
          StoreRegisterForm form = StoreRegisterForm.builder()
                  .storeName("가게이름")
                  .storeDescription("가게설명")
                  .location("가게위치")
                  .keyword("카페")
                  .build();

          MockHttpSession mockSession = new MockHttpSession();
          mockSession.setAttribute(SessionConst.LOGIN_PARTNER, null);

          MockMvc testMock = MockMvcBuilders
                  .standaloneSetup(new StoreController(storeService))
                  .build();

          //when //then
          testMock.perform(post("/store/register")
                  .session(mockSession)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(form)))
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

        given(storeService.detailStore(anyLong()))
                .willReturn(new StoreDetailDto("desc"));

        //when //then
        testMock.perform(get("/store/detail/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeDescription").value("desc"));
     }

}
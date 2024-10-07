package reservation.hmw.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reservation.hmw.exception.CustomException;
import reservation.hmw.exception.ErrorCode;
import reservation.hmw.model.entity.Partner;
import reservation.hmw.model.entity.Store;
import reservation.hmw.model.entity.dto.StoreDetailDto;
import reservation.hmw.model.entity.dto.StoreInfoDto;
import reservation.hmw.model.entity.dto.StoreRegisterForm;
import reservation.hmw.repository.PartnerRepository;
import reservation.hmw.repository.StoreRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreService storeService;

    @Test
    void registerStore_success() {
        StoreRegisterForm mockForm = StoreRegisterForm.builder()
                .storeName("name")
                .location("seoul")
                .storeDescription("desc")
                .build();

        Partner mockPartner = Partner.builder()
                .email("asdf@naver.com")
                .name("홍길동")
                .password("1234")
                .storeList(new ArrayList<>())
                .build();

        // given
        given(partnerRepository.findById(anyLong()))
                .willReturn(Optional.of(mockPartner));

        given(storeRepository.save(any()))
                .willReturn(Store.builder()
                        .storeName(mockForm.getStoreName())
                        .partner(mockPartner)
                        .storeDescription(mockForm.getStoreDescription())
                        .location(mockForm.getLocation())
                        .build());

        // when
        Store actual = storeService.registerStore(mockForm, 123L);

        // then
        Assertions.assertThat(actual.getStoreName()).isEqualTo("name");
        Assertions.assertThat(actual.getStoreDescription()).isEqualTo("desc");
        Assertions.assertThat(actual.getLocation()).isEqualTo("seoul");
        Assertions.assertThat(actual.getPartner()).isEqualTo(mockPartner);
    }

    @Test
    void registerStore_PARTNER_ONLY_ACCESS() {
        //given
        StoreRegisterForm mockForm = StoreRegisterForm.builder()
                .storeName("name")
                .location("seoul")
                .storeDescription("desc")
                .build();

        //when
        CustomException exception = assertThrows(CustomException.class,
                () -> storeService.registerStore(mockForm, 1L));

        //then
        Assertions.assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.PARTNER_ACCESS_ONLY);
     }

     @Test
     void searchStoreByKeyword_success() {
         //given
         List<Store> storeList = Arrays.asList(
                 Store.builder()
                         .storeName("chicken")
                         .location("seoul")
                         .storeDescription("good")
                         .keyword("keyword")
                         .build(),
                 Store.builder()
                         .storeName("pizza")
                         .location("zip")
                         .storeDescription("shit")
                         .keyword("keyword")
                         .build());

         given(storeRepository.findAllByKeyword(any()))
                 .willReturn(Optional.of(storeList));

         //when
         List<StoreInfoDto> storeInfoDtoList = storeService.searchStoreByKeyword("keyword");

         //then
         Assertions.assertThat(storeInfoDtoList.size()).isEqualTo(2);
      }

      @Test
      void searchStoreByKeyword_NOT_FOUND_STORE() {
          //given
          given(storeRepository.findAllByKeyword(anyString()))
                  .willReturn(Optional.empty());

          //when
          CustomException exception = assertThrows(CustomException.class,
                  () -> storeService.searchStoreByKeyword("test"));

          //then
          Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_STORE);
       }

       @Test
       void searchStoreByName_success() {
           //given
           Store store = Store.builder()
                   .storeName("chicken")
                   .location("seoul")
                   .storeDescription("good")
                   .keyword("keyword")
                   .build();

           given(storeRepository.findByStoreName(anyString()))
                   .willReturn(Optional.of(store));

           //when
           StoreInfoDto storeInfoDto = storeService.searchStoreByName("test");

           //then
           Assertions.assertThat(storeInfoDto.getStoreName()).isEqualTo("chicken");
           Assertions.assertThat(storeInfoDto.getLocation()).isEqualTo("seoul");
           Assertions.assertThat(storeInfoDto.getKeyword()).isEqualTo("keyword");
        }

        @Test
        void detailStore_success() {
            //given
            Store store = Store.builder()
                    .storeName("chicken")
                    .location("seoul")
                    .storeDescription("good")
                    .keyword("keyword")
                    .build();

            given(storeRepository.findById(anyLong()))
                    .willReturn(Optional.of(store));

            //when
            StoreDetailDto storeDetailDto = storeService.detailStore(1L);

            //then
            Assertions.assertThat(storeDetailDto.getStoreDescription())
                    .isEqualTo("good");
         }

         @Test
         void detailStore_NOT_FOUND_STORE() {
             //given
             given(storeRepository.findById(anyLong()))
                     .willReturn(Optional.empty());

             //when
             CustomException exception = assertThrows(CustomException.class,
                     () -> storeService.detailStore(1L));

             //then
             Assertions.assertThat(exception.getErrorCode())
                     .isEqualTo(ErrorCode.NOT_FOUND_STORE);
          }

    @Test
    void updateStore_success() {
        // given
        Partner mockPartner = Partner.builder()
                .id(1L)
                .email("asdf@naver.com")
                .name("홍길동")
                .password("1234")
                .storeList(new ArrayList<>())
                .build();

        Store existingStore = Store.builder()
                .id(1L)
                .storeName("name")
                .location("seoul")
                .storeDescription("good")
                .keyword("keyword")
                .partner(mockPartner)
                .build();

        StoreRegisterForm updateForm = new StoreRegisterForm();
        updateForm.setStoreName("Updated Store Name");
        updateForm.setStoreDescription("Updated Description");
        updateForm.setLocation("Updated Location");
        updateForm.setKeyword("Updated Keyword");

        given(storeRepository.findById(1L)).willReturn(Optional.of(existingStore));
        given(storeRepository.save(any(Store.class))).willReturn(existingStore);

        // when
        Store updatedStore = storeService.updateStore(1L, updateForm, 1L);

        // then
        Assertions.assertThat(updatedStore.getStoreName()).isEqualTo("Updated Store Name");
        Assertions.assertThat(updatedStore.getStoreDescription()).isEqualTo("Updated Description");
        Assertions.assertThat(updatedStore.getLocation()).isEqualTo("Updated Location");
        Assertions.assertThat(updatedStore.getKeyword()).isEqualTo("Updated Keyword");

        verify(storeRepository, times(1)).findById(1L);
        verify(storeRepository, times(1)).save(any(Store.class));
    }


    @Test
    void updateStore_unauthorized() {
        // given
        Partner mockPartner = Partner.builder()
                .email("asdf@naver.com")
                .name("홍길동")
                .password("1234")
                .storeList(new ArrayList<>())
                .build();

        Store existingStore = Store.builder()
                .id(1L)
                .storeName("name")
                .location("seoul")
                .storeDescription("good")
                .keyword("keyword")
                .partner(mockPartner)
                .build();

        StoreRegisterForm updateForm = new StoreRegisterForm();

        given(storeRepository.findById(1L)).willReturn(Optional.of(existingStore));

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
                storeService.updateStore(1L, updateForm, 1L)); // 다른 파트너 ID로 업데이트 시도

        // then
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED_ACTION);
        verify(storeRepository, times(1)).findById(1L);
    }

    @Test
    void deleteStore_success() {
        // given
        Partner mockPartner = Partner.builder()
                .id(1L)
                .email("asdf@naver.com")
                .name("홍길동")
                .password("1234")
                .storeList(new ArrayList<>())
                .build();

        Store existingStore = Store.builder()
                .id(1L)
                .storeName("name")
                .location("seoul")
                .storeDescription("good")
                .keyword("keyword")
                .partner(mockPartner)
                .build();

        given(storeRepository.findById(1L)).willReturn(Optional.of(existingStore));

        // when
        storeService.deleteStore(1L, 1L); // 올바른 파트너 ID로 삭제 호출

        // then
        verify(storeRepository, times(1)).findById(1L);
        verify(storeRepository, times(1)).delete(existingStore);
    }


    @Test
    void deleteStore_unauthorized() {
        // given
        Partner mockPartner = Partner.builder()
                .email("asdf@naver.com")
                .name("홍길동")
                .password("1234")
                .storeList(new ArrayList<>())
                .build();

        Store existingStore = Store.builder()
                .id(1L)
                .storeName("name")
                .location("seoul")
                .storeDescription("good")
                .keyword("keyword")
                .partner(mockPartner) // 다른 파트너 ID
                .build();

        given(storeRepository.findById(1L)).willReturn(Optional.of(existingStore));

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
                storeService.deleteStore(1L, 1L)); // 다른 파트너 ID로 삭제 시도

        // then
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED_ACTION);
        verify(storeRepository, times(1)).findById(1L);
    }
}
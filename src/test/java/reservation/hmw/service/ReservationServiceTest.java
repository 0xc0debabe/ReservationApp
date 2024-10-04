package reservation.hmw.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reservation.hmw.exception.CustomException;
import reservation.hmw.exception.ErrorCode;
import reservation.hmw.model.entity.Reservation;
import reservation.hmw.model.entity.Store;
import reservation.hmw.model.entity.User;
import reservation.hmw.model.entity.dto.ReservationDto;
import reservation.hmw.model.entity.enums.ReservationStatus;
import reservation.hmw.repository.ReservationRepository;
import reservation.hmw.repository.StoreRepository;
import reservation.hmw.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void createReservation_success() {
        //given
        ReservationDto.Request request = ReservationDto.Request.builder()
                .userId(1L)
                .storeId(1L)
                .reservationTime(LocalDateTime.now())
                .build();

        Store mockStore = Store.builder()
                .storeName("store")
                .location("seoul")
                .build();

        User mockUser = User.builder()
                .name("kim")
                .build();

        Reservation mockRes = Reservation.builder()
                .store(mockStore)
                .user(mockUser)
                .reservationTime(LocalDateTime.now())
                .reservationStatus(ReservationStatus.PENDING)
                .build();

        given(storeRepository.findById(anyLong()))
                .willReturn(Optional.of(mockStore));

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(mockUser));

        given(reservationRepository.save(any()))
                .willReturn(mockRes);

        //when
        ReservationDto.Response actual = reservationService.createReservation(request);

        //then
        Assertions.assertThat(actual.getLocation()).isEqualTo("seoul");
        Assertions.assertThat(actual.getStoreName()).isEqualTo("store");
        Assertions.assertThat(actual.getUserName()).isEqualTo("kim");
     }

    @Test
    void createReservation_NOT_FOUND_STORE() {
        //given
        ReservationDto.Request request = ReservationDto.Request.builder()
                .userId(1L)
                .storeId(1L)
                .reservationTime(LocalDateTime.now())
                .build();

        given(storeRepository.findById(anyLong()))
                .willReturn(Optional.empty());


        //when
        CustomException exception = assertThrows(CustomException.class,
                () -> reservationService.createReservation(request));

        //then
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_STORE);
    }

    @Test
    void createReservation_NOT_FOUND_USER() {
        //given
        ReservationDto.Request request = ReservationDto.Request.builder()
                .userId(1L)
                .storeId(1L)
                .reservationTime(LocalDateTime.now())
                .build();

        Store mockStore = Store.builder()
                .storeName("store")
                .location("seoul")
                .build();

        given(storeRepository.findById(anyLong()))
                .willReturn(Optional.of(mockStore));


        //when
        CustomException exception = assertThrows(CustomException.class,
                () -> reservationService.createReservation(request));

        //then
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_USER);
    }

}
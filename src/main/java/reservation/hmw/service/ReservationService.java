package reservation.hmw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public ReservationDto.Response createReservation(ReservationDto.Request dtoRequest) {
        Store findStore = storeRepository.findById(dtoRequest.getStoreId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_STORE));

        User findUser = userRepository.findById(dtoRequest.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Reservation reservation = reservationRepository.save(Reservation.builder()
                .store(findStore)
                .user(findUser)
                .reservationTime(dtoRequest.getReservationTime())
                .reservationStatus(ReservationStatus.PENDING)
                .build());

        return ReservationDto.Response.builder()
                .storeName(findStore.getStoreName())
                .userName(findUser.getName())
                .location(findStore.getLocation())
                .reservationTime(reservation.getReservationTime())
                .build();
    }

}

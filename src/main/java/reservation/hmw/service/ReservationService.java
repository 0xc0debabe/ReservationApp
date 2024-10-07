package reservation.hmw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reservation.hmw.exception.CustomException;
import reservation.hmw.exception.ErrorCode;
import reservation.hmw.model.entity.Reservation;
import reservation.hmw.model.entity.Store;
import reservation.hmw.model.entity.User;
import reservation.hmw.model.entity.dto.ConfirmReservationDto;
import reservation.hmw.model.entity.dto.ReservationDto;
import reservation.hmw.model.entity.enums.ReservationStatus;
import reservation.hmw.repository.ReservationRepository;
import reservation.hmw.repository.StoreRepository;
import reservation.hmw.repository.UserRepository;

import java.time.LocalDateTime;

/**
 * 예약 서비스 클래스입니다.
 * 예약 생성 및 관련된 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    /**
     * 예약을 생성하는 메서드입니다.
     *
     * @param dtoRequest 예약 생성 요청 정보를 포함하는 DTO
     * @return 예약 생성 결과를 포함하는 응답 DTO
     * @throws CustomException 매장이나 사용자를 찾을 수 없는 경우 예외를 발생시킵니다.
     */
    @Transactional
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

        findStore.getReservationList().add(reservation);

        return ReservationDto.Response.builder()
                .storeName(findStore.getStoreName())
                .userName(findUser.getName())
                .location(findStore.getLocation())
                .reservationTime(reservation.getReservationTime())
                .build();
    }

    public ConfirmReservationDto.Response confirmReservation(ConfirmReservationDto.Request confirmForm) {
        User findUser = userRepository.findByPhone(confirmForm.getPhone())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Reservation findReservation = reservationRepository.findByUserId(findUser.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (findReservation.getReservationStatus() != ReservationStatus.APPROVED) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_APPROVED);
        }

        Store findStore = findReservation.getStore();

        return ConfirmReservationDto.Response.builder()
                .userName(findUser.getName())
                .storeName(findStore.getStoreName())
                .reservationTime(findReservation.getReservationTime())
                .build();
    }

    @Transactional
    public void approveReservation(Long reservationId) {
        Reservation findReservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        findReservation.setReservationStatus(ReservationStatus.APPROVED);
        reservationRepository.save(findReservation);
    }

    @Transactional
    public void rejectReservation(Long reservationId) {
        Reservation findReservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        findReservation.setReservationStatus(ReservationStatus.REJECT);
        reservationRepository.save(findReservation);
    }

}

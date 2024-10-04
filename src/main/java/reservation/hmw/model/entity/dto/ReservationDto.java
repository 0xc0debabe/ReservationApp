package reservation.hmw.model.entity.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import reservation.hmw.model.entity.Reservation;

import java.time.LocalDateTime;


public class ReservationDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @NotNull
        private Long storeId;

        @NotNull
        private Long userId;

        @NotNull
        @Future(message = "예약 시간은 현재 시간보다 미래여야 합니다.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime reservationTime;

    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        private String storeName;
        private String location;
        private String userName;
        private LocalDateTime reservationTime;

    }

}

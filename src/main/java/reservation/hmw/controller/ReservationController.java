package reservation.hmw.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reservation.hmw.Validation;
import reservation.hmw.model.entity.dto.ConfirmReservationDto;
import reservation.hmw.model.entity.dto.ReservationDto;
import reservation.hmw.service.ReservationService;


/**
 * 예약 관련 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("reservation")
public class ReservationController {

    private final ReservationService reservationService;


    /**
     * @param dtoRequest 예약 생성 요청 정보를 포함하는 DTO
     * @param bindingResult 유효성 검사 결과
     * @return 예약 생성 결과를 포함하는 응답
     */
    @PostMapping()
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationDto.Request dtoRequest,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Validation.getErrorResponse(bindingResult);
        }

        ReservationDto.Response reservation = reservationService.createReservation(dtoRequest);
        return ResponseEntity.ok(reservation);
    }

    @PostMapping("confirm")
    public ResponseEntity<?> confirmReservation(@Valid @RequestBody ConfirmReservationDto.Request confirmForm,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Validation.getErrorResponse(bindingResult);
        }

        return ResponseEntity.ok(reservationService.confirmReservation(confirmForm));
    }

    @PutMapping("/approve/{reservationId}")
    public ResponseEntity<?> approveReservation(@PathVariable(name = "reservationId") Long reservationId) {
        reservationService.approveReservation(reservationId);
        return ResponseEntity.ok("APPROVED");
    }

    @PutMapping("/reject/{reservationId}")
    public ResponseEntity<?> rejectReservation(@PathVariable(name = "reservationId") Long reservationId) {
        reservationService.rejectReservation(reservationId);
        return ResponseEntity.ok("REJECTED");
    }

}

package reservation.hmw.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reservation.hmw.Validation;
import reservation.hmw.model.entity.dto.ReservationDto;
import reservation.hmw.service.ReservationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping()
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationDto.Request dtoRequest,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Validation.getErrorResponse(bindingResult);
        }

        ReservationDto.Response reservation = reservationService.createReservation(dtoRequest);
        return ResponseEntity.ok(reservation);
    }

}

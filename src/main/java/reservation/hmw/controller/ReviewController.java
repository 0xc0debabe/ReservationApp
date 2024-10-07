package reservation.hmw.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reservation.hmw.Validation;
import reservation.hmw.exception.CustomException;
import reservation.hmw.exception.ErrorCode;
import reservation.hmw.model.entity.dto.ReviewForm;
import reservation.hmw.model.entity.session.SessionConst;
import reservation.hmw.service.ReviewService;

@RestController
@RequiredArgsConstructor
@RequestMapping("review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewForm.Request formRequest,
                                          BindingResult bindingResult,
                                          HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Validation.getErrorResponse(bindingResult);
        }

        Long userId = (Long) request.getSession().getAttribute(SessionConst.LOGIN_USER);
        if (userId == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_USER);
        }

        return ResponseEntity.ok(reviewService.createReview(formRequest, userId));
    }

    @GetMapping("{reviewId}")
    public ResponseEntity<?> getReview(@PathVariable(name = "reviewId") Long reviewId) {
        return ResponseEntity.ok(reviewService.getReview(reviewId));
    }

    @GetMapping()
    public ResponseEntity<?> getReviewsByStore(
            @RequestParam(name = "storeId") Long storeId,
            @RequestParam(name = "page", defaultValue = "0") int page) {

        Page<ReviewForm.Response> reviews = reviewService.getReviewsByStore(storeId, page);
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("{reviewId}")
    public ResponseEntity<?> updateReview(@Valid @RequestBody ReviewForm.Request formRequest,
                                          BindingResult bindingResult,
                                          @PathVariable(name = "reviewId") Long reviewId,
                                          @SessionAttribute(SessionConst.LOGIN_USER) Long userId) {
        if (bindingResult.hasErrors()) {
            return Validation.getErrorResponse(bindingResult);
        }

        return ResponseEntity.ok(reviewService.updateReview(formRequest, reviewId, userId));
    }

    @DeleteMapping("{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable(name = "reviewId") Long reviewId,
                                          HttpServletRequest request) {
        Long userId = (Long) request.getSession(false).getAttribute("userId");
        Long partnerId = (Long) request.getSession(false).getAttribute("partnerId");

        if (userId == null && partnerId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        reviewService.deleteReview(reviewId, userId, partnerId);
        return ResponseEntity.ok("DELETE");
    }

}

package reservation.hmw.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reservation.hmw.exception.CustomException;
import reservation.hmw.exception.ErrorCode;
import reservation.hmw.model.entity.Review;
import reservation.hmw.model.entity.Store;
import reservation.hmw.model.entity.User;
import reservation.hmw.model.entity.dto.ReviewForm;
import reservation.hmw.repository.ReviewRepository;
import reservation.hmw.repository.StoreRepository;
import reservation.hmw.repository.UserRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public ReviewForm.Response createReview(ReviewForm.Request formRequest, Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Store findStore = storeRepository.findById(formRequest.getStoreId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_STORE));

        Review review = reviewRepository.save(Review.builder()
                .rating(formRequest.getRating())
                .content(formRequest.getContent())
                .user(findUser)
                .store(findStore)
                .build());

        return ReviewForm.Response.fromEntity(review);
    }

    public ReviewForm.Response getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

        return ReviewForm.Response.fromEntity(review);
    }

    public Page<ReviewForm.Response> getReviewsByStore(Long storeId, int page) {
        Pageable pageable = PageRequest.of(page, 15, Sort.by("createAt").descending());
        Page<Review> reviews = reviewRepository.findByStoreId(storeId, pageable);

        return reviews.map(ReviewForm.Response::fromEntity);
    }

    @Transactional
    public ReviewForm.Response updateReview(ReviewForm.Request formRequest, Long reviewId, Long userId) {
        Review findReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

        if (!Objects.equals(findReview.getUser().getId(), userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        findReview.setRating(formRequest.getRating());
        findReview.setContent(formRequest.getContent());


        return ReviewForm.Response.fromEntity(findReview);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId, Long partnerId) {
        Review findReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

        if (userId != null && !findReview.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        if (partnerId != null && !findReview.getStore().getPartner().getId().equals(partnerId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        reviewRepository.delete(findReview);
    }

}

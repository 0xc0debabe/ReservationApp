package reservation.hmw.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import reservation.hmw.exception.CustomException;
import reservation.hmw.exception.ErrorCode;
import reservation.hmw.model.entity.Review;
import reservation.hmw.model.entity.Store;
import reservation.hmw.model.entity.User;
import reservation.hmw.model.entity.dto.ReviewForm;
import reservation.hmw.repository.ReviewRepository;
import reservation.hmw.repository.StoreRepository;
import reservation.hmw.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;

    private User user;
    private Store store;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("user")
                .build();

        store = Store.builder()
                .id(1L)
                .build();
    }

    @Test
    void createReview_success() {
        //given
        ReviewForm.Request formRequest = new ReviewForm.Request();
        formRequest.setStoreId(store.getId());
        formRequest.setRating(5);
        formRequest.setContent("Great!");

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(storeRepository.findById(store.getId())).willReturn(Optional.of(store));

        Review savedReview = Review.builder()
                .id(1L)
                .rating(formRequest.getRating())
                .content(formRequest.getContent())
                .user(user)
                .store(store)
                .build();

        given(reviewRepository.save(any(Review.class))).willReturn(savedReview);

        //when
        ReviewForm.Response response = reviewService.createReview(formRequest, 1L);

        //then
        Assertions.assertEquals(response.getRating(), 5);
        Assertions.assertEquals(response.getContent(), "Great!");
        Assertions.assertEquals(response.getUserName(), "user");
    }

    @Test
    void getReview_success() {
        //given
        Review review = Review.builder()
                .id(1L)
                .rating(5)
                .content("Great!")
                .user(user)
                .store(store)
                .build();

        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

        //when
        ReviewForm.Response response = reviewService.getReview(1L);

        //then
        Assertions.assertEquals(response.getRating(), 5);
        Assertions.assertEquals(response.getContent(), "Great!");
    }

    @Test
    void getReview_NOT_FOUND_REVIEW() {
        //given
        given(reviewRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        CustomException exception = Assertions.assertThrows(CustomException.class, () -> reviewService.getReview(1L));

        //then
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.NOT_FOUND_REVIEW);
    }

    @Test
    void getReviewsByStore_success() {
        //given
        Review review1 = Review.builder()
                .id(1L)
                .rating(5)
                .content("Great!")
                .user(user)
                .store(store)
                .build();

        Review review2 = Review.builder()
                .id(2L)
                .rating(4)
                .content("Good!")
                .user(user)
                .store(store)
                .build();

        Page<Review> reviewPage = new PageImpl<>(List.of(review1, review2));

        given(reviewRepository.findByStoreId(anyLong(), any(Pageable.class))).willReturn(reviewPage);

        //when
        Page<ReviewForm.Response> responsePage = reviewService.getReviewsByStore(store.getId(), 0);

        //then
        Assertions.assertEquals(responsePage.getContent().size(), 2);
        Assertions.assertEquals(responsePage.getContent().get(0).getRating(), 5);
        Assertions.assertEquals(responsePage.getContent().get(1).getRating(), 4);
    }

    @Test
    void updateReview_success() {
        //given
        ReviewForm.Request formRequest = new ReviewForm.Request();
        formRequest.setRating(5);
        formRequest.setContent("Updated!");

        Review existingReview = Review.builder()
                .id(1L)
                .rating(4)
                .content("Good!")
                .user(user)
                .store(store)
                .build();

        given(reviewRepository.findById(1L)).willReturn(Optional.of(existingReview));

        //when
        ReviewForm.Response response = reviewService.updateReview(formRequest, 1L, 1L);

        //then
        Assertions.assertEquals(response.getRating(), 5);
        Assertions.assertEquals(response.getContent(), "Updated!");
    }

    @Test
    void updateReview_NOT_FOUND_REVIEW() {
        //given
        given(reviewRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        CustomException exception = Assertions.assertThrows(CustomException.class, () -> reviewService.updateReview(new ReviewForm.Request(), 1L, 1L));

        //then
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.NOT_FOUND_REVIEW);
    }

    @Test
    void deleteReview_success() {
        //given
        Review existingReview = Review.builder()
                .id(1L)
                .user(user)
                .store(store)
                .build();

        given(reviewRepository.findById(1L)).willReturn(Optional.of(existingReview));

        //when
        reviewService.deleteReview(1L, 1L, null);

        //then
        verify(reviewRepository, times(1)).delete(existingReview);
    }

    @Test
    void deleteReview_NOT_FOUND_REVIEW() {
        //given
        given(reviewRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        CustomException exception = Assertions.assertThrows(CustomException.class, () -> reviewService.deleteReview(1L, 1L, null));

        //then
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.NOT_FOUND_REVIEW);
    }

    @Test
    void deleteReview_UNAUTHORIZED_ACTION() {
        //given
        Review existingReview = Review.builder()
                .id(1L)
                .user(user)
                .store(store)
                .build();

        given(reviewRepository.findById(1L)).willReturn(Optional.of(existingReview));

        //when
        CustomException exception = Assertions.assertThrows(CustomException.class, () -> reviewService.deleteReview(1L, 2L, null));

        //then
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.UNAUTHORIZED_ACTION);
    }
}
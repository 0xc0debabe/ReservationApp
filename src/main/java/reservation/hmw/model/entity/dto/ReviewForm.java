package reservation.hmw.model.entity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import reservation.hmw.model.entity.Review;

public class ReviewForm {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @Min(value = 1)
        @Max(value = 5)
        private Integer rating;

        @NotBlank
        private String content;

        private Long storeId;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {

        private String userName;
        private String storeName;
        private Integer rating;
        private String content;

        public static ReviewForm.Response fromEntity(Review review) {

            return ReviewForm.Response.builder()
                    .storeName(review.getStore().getStoreName())
                    .rating(review.getRating())
                    .content(review.getContent())
                    .userName(review.getUser().getName())
                    .build();
        }
    }
}

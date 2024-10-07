package reservation.hmw.model.entity.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import reservation.hmw.model.entity.Reservation;
import reservation.hmw.model.entity.Review;

import java.util.List;

@Getter
@AllArgsConstructor
public class StoreDetailDto {

    private String storeDescription;
    private List<Review> reviewList;

}

package reservation.hmw.model.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import reservation.hmw.model.entity.Store;

@Getter
@AllArgsConstructor
@Builder
public class StoreInfoDto {

    private String storeName;
    private String location;
    private String keyword;

    public static StoreInfoDto fromEntity(Store store) {
        return StoreInfoDto.builder()
                .storeName(store.getStoreName())
                .location(store.getLocation())
                .keyword(store.getKeyword())
                .build();
    }

}

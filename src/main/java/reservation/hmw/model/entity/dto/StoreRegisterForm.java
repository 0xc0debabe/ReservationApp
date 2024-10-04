package reservation.hmw.model.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import reservation.hmw.model.entity.Store;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StoreRegisterForm {

    @NotBlank
    private String storeName;

    @NotBlank
    private String location;

    @NotBlank
    private String storeDescription;

    @NotBlank
    private String keyword;

}

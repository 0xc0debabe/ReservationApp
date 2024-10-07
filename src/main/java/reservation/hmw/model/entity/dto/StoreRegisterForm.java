package reservation.hmw.model.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import reservation.hmw.model.entity.Store;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
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

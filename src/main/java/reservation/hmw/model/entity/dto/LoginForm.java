package reservation.hmw.model.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class LoginForm {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        @Email
        private String email;

        @NotBlank
        private String password;

    }

    @Builder
    @Getter
    public static class Response {

        private Long id;
        private String email;
        private String name;

    }
}

package reservation.hmw.model.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import reservation.hmw.model.entity.Partner;
import reservation.hmw.model.entity.User;

public class RegisterForm {


    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        @NotEmpty
        private String name;

        @Email
        private String email;

        @NotEmpty
        private String password;

        public Partner toPartnerEntity() {
            return Partner.builder()
                    .name(this.name)
                    .email(this.email)
                    .password(this.password)
                    .build();
        }

        public User toUserEntity() {
            return User.builder()
                    .name(this.name)
                    .email(this.email)
                    .password(this.password)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class Response {

        private String name;
        private String email;

    }

}

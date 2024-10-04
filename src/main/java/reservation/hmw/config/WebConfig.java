package reservation.hmw.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import reservation.hmw.model.entity.session.LoginCheckInterceptor;
import reservation.hmw.model.entity.session.PartnerCheckInterceptor;

/**
 * 세션 인터셉터를 위한 설정입니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PartnerCheckInterceptor())
                .order(1)
                .addPathPatterns("/store/register");

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/reservation/**");
    }

}

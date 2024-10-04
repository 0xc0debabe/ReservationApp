package reservation.hmw.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 감사 기능 활성화
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
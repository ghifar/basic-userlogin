package com.ghifar.userlogin.userlogin.config;
/*
 * Audit Configuration
 * To enable JPA Auditing, we’ll need to add @EnableJpaAuditing annotation to our main class or any other configuration classes.
 * this class is related to our audit Model class -> DateAudit and UserDateAudit
 * NOTE FROM ABU: In order to get your Auditing @CreatedDate,etc to work,,,, you must implements AuditorAware from spring.data (https://stackoverflow.com/questions/20483841/spring-data-createddate-annotation-doesnt-work-for-me)
 *  or you can just write like this AuditingConfig class (i thinks its just the same as the spring docs.)
 * basically we're gonna override getCurrentAuditor from AuditorAware.
 * */

import com.ghifar.userlogin.userlogin.security.UserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {

    @Bean
    public AuditorAware<Long> auditorProvider(){
        return new CustomSpringSecurityAwareImpl();
    }

    class CustomSpringSecurityAwareImpl implements AuditorAware<Long>{

        @Override
        public Optional<Long> getCurrentAuditor() {
            Authentication authentication= SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken){
                return Optional.empty();

            }

            UserPrincipal userPrincipal= (UserPrincipal) authentication.getPrincipal();

            return Optional.ofNullable(userPrincipal.getId());
        }
    }

}

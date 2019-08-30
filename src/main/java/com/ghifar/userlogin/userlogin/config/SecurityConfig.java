package com.ghifar.userlogin.userlogin.config;
/* This class for security Configuration*/

import com.ghifar.userlogin.userlogin.security.CustomUserDetailsService;
import com.ghifar.userlogin.userlogin.security.JwtAuthenticationEntryPoint;
import com.ghifar.userlogin.userlogin.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity //to enable web security in porject
@EnableGlobalMethodSecurity(
        //to enable method level security based on annotations.
        securedEnabled = true,//usage = @Secured("ROLE") - to protect service/controller method.
        jsr250Enabled = true,//usage = @RolesAllowed()
        prePostEnabled = true// usage = @PreAuthorize("isAnonymous()") OR @PostAuthorize("hasRole('USER')") - enables more complex expression based access control syntax
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    //this custom variable used to handle returned exception as 401 unauthorized error to clients that try to access protected resource.
    // this will be set at spring security configuration
    //it implements spring security module AuthenticationEntryPoint
    //custom class implements spring security module AuthenticationEntryPoint
    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;


    //custom class implements Onceperrequest
    //it will reads jwt Authentication from Authorization header from every requests
    //validate token if its valid or not
    //load user details baed on its token(jwt)
    //Sets the user details in Spring Security’s SecurityContext. Spring Security uses the user details to perform authorization checks. We can also access the user details stored in the SecurityContext in our controllers to perform our business logic.
    //its basically implements doFilter method.
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter();
    }

    //AuthenticationManagerBuilder is used to create an AuthenticationManager instance which is the main Spring Security interface for authenticating a user.
    // can be used to build in-memory authentication, LDAP, authentication, etc..
    //In this code, we’ve provided our customUserDetailsService and a passwordEncoder to build the AuthenticationManager.
    //We’ll use the configured AuthenticationManager to authenticate a user in the login API.
    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    //will run at compiletime
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests().antMatchers("/","/favicon.ico","/**/*.png", "/**/*.gif", "/**/*.svg", "/**/*.jpg", "/**/*.html","/**/*.css","/**/*.js")
                .permitAll()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/user/checkUsernameAvailability", "/api/user/checkEmailAvailability").permitAll()
                .antMatchers(HttpMethod.GET, "/api/polls/**", "/api/users/**").permitAll()
                .anyRequest()
                .authenticated();

        //Our custom JWT security Filter
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


}

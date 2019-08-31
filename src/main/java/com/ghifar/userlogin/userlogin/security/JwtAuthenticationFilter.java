package com.ghifar.userlogin.userlogin.security;

/*this class is used for to get JWT token from request -> validate it -> load the user that associated with the token -> then pass it spring security
* */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private static final Logger logger= LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    //every call to API that executed will execute this method first
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt= getJwtFromRequest(httpServletRequest);
            //this if statement only execute when user already logged in :)))
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)){
                Long userId= tokenProvider.getUserIdFromJwt(jwt);

                UserDetails userDetails= customUserDetailsService.loadByUserId(userId);

                //after this, we're gonna set authentication in spring security context... based on our userDetails.

                //read related article https://stackoverflow.com/questions/56318398/does-it-necessary-to-put-original-credentials-to-usernamepasswordauthenticationt
                UsernamePasswordAuthenticationToken authentication= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                //whats WebAuthenticationDetailsSource() really do?
                //here's the related problem i found on Github (https://github.com/philipsorst/angular-rest-springsecurity/issues/21)
                //authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                //this SecurityContextHolder using ThreadLocal. see docs i think it will holds the authorities and userDetails and will be use to know if the user authorize for some api...
                //if you dont set this, you wont able to pass the authorization to access some API when doFilter execute it's job
                SecurityContextHolder.getContext().setAuthentication(authentication);


            }
        }catch (Exception e){
            logger.error("Response from JwtAuthenticationFilter Class = Couldnt set user authentication in spring security context {}",e);
        }

        //doFilter() method is invoked every time when user request to any resource, to which the filter is mapped.It is used to perform filtering tasks.
        //(http://otndnld.oracle.co.jp/document/products/as10g/101300/B25221_03/web.1013/b14426/filters.htm)
        //(https://www.javacodegeeks.com/2018/02/securitycontext-securitycontextholder-spring-security.html) < its just the context. there are some orders executed based on spring docs.
        //same context > (https://www.javatpoint.com/servlet-filter)
        //another example (https://stackoverflow.com/questions/1323009/is-dofilter-executed-before-or-after-the-servlets-work-is-done)
        //https://stackoverflow.com/questions/41480102/how-spring-security-filter-chain-works
        //basically this method will run all filter including security purpose to exexuted all that we've been set above
        //this method will throw all security exception before controller gets executed
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }


    private String getJwtFromRequest(HttpServletRequest request){
        String bearerToken= request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7, bearerToken.length());
        }

        return null;
    }
}

package com.ghifar.userlogin.userlogin.security;

import com.ghifar.userlogin.userlogin.model.User;
import com.ghifar.userlogin.userlogin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    //we override this method so user can login either using email or username
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        //Let people login with either username or email
        User user= userRepository.findByUsernameOrEmail(s, s).orElseThrow(() -> new UsernameNotFoundException("User Not found with username or email = "+s));

        return UserPrincipal.create(user);
    }

    //this method will used by JWTAuthenticationFilter class
    @Transactional
    public UserDetails loadByUserId(Long id){
        User user= userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not Fund with id = "+id));

        return UserPrincipal.create(user);

    }

}

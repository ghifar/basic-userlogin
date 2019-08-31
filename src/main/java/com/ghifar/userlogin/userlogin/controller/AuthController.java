package com.ghifar.userlogin.userlogin.controller;

import com.ghifar.userlogin.userlogin.customEnum.RoleName;
import com.ghifar.userlogin.userlogin.customException.AppException;
import com.ghifar.userlogin.userlogin.dto.ApiResponse;
import com.ghifar.userlogin.userlogin.dto.JwtAuthenticationResponse;
import com.ghifar.userlogin.userlogin.dto.LoginRequest;
import com.ghifar.userlogin.userlogin.dto.SignUpRequest;
import com.ghifar.userlogin.userlogin.model.Role;
import com.ghifar.userlogin.userlogin.model.User;
import com.ghifar.userlogin.userlogin.repository.RoleRepository;
import com.ghifar.userlogin.userlogin.repository.UserRepository;
import com.ghifar.userlogin.userlogin.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        // actually this method name is self explained.. but the flow is quite confusing for me.
        // this authenticate method is used for load user by their credentials(username, password) .. username and password if its valid or not
        //we're using our customUserDetailService that we provide from @overriding configure() method in SecurityConfig class
        // this authenticate method will perform class that implements UserDetailsService that we override in CustomUserDetailsService class.
        // read(https://stackoverflow.com/questions/9787409/what-is-the-default-authenticationmanager-in-spring-security-how-does-it-authen)
        Authentication authentication= authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(),loginRequest.getPassword()));

        //notice that this method execute at JwtAuthenticationFilter class too..
        // idk what's the use we execute this in here.....
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt= tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest){
        if (userRepository.existsByUsername(signUpRequest.getUsername())){
            return new ResponseEntity(new ApiResponse(false, "username is already taken!"), HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())){
            return new ResponseEntity(new ApiResponse(false, "email alraedy use!"), HttpStatus.BAD_REQUEST);
        }

        //create the user

        User user = new User(signUpRequest.getName(),signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getPassword());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //if errors happened, dont forget to create it in database ROLE_USER and etc.. in roles table
        Role userRole= roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set"));

        user.setRoles(Collections.singleton(userRole));

        User result= userRepository.save(user);

        // this is for response headers (we'll put it at Location headers) that we sent back to client.
        URI location= ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User Registered Successfully"));

    }
}

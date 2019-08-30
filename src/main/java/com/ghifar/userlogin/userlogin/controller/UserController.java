package com.ghifar.userlogin.userlogin.controller;

import com.ghifar.userlogin.userlogin.customException.ResourceNotFoundException;
import com.ghifar.userlogin.userlogin.dto.UserIdentityAvailability;
import com.ghifar.userlogin.userlogin.dto.UserProfile;
import com.ghifar.userlogin.userlogin.dto.UserSummary;
import com.ghifar.userlogin.userlogin.model.User;
import com.ghifar.userlogin.userlogin.repository.UserRepository;
import com.ghifar.userlogin.userlogin.security.CurrentUser;
import com.ghifar.userlogin.userlogin.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser){
        UserSummary userSummary= new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName());
        return userSummary;
    }

    @GetMapping("/user/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username){
        Boolean isAvailable= !userRepository.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
        //true or false
    }

    @GetMapping("user/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email){
        Boolean isAvailable= !userRepository.existsByEmail(email);

        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/users/{username}")
    public UserProfile getUserProfile(@PathVariable(value = "username") String username){
        User user= userRepository.findByUsername(username)
                .orElseThrow(() ->new ResourceNotFoundException("User", "username", username));

        UserProfile userProfile= new UserProfile(user.getId(), user.getUsername(), user.getName(),user.getCreatedAt());

        return userProfile;
    }
    

}

package com.benimsin.recipemanagementservice.controller;

import com.benimsin.recipemanagementservice.model.User;
import com.benimsin.recipemanagementservice.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserController(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping(value = "/signup")
    public void signUp(@RequestBody User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @PutMapping(value = "/updateUser")
    public void updateUser(@RequestBody User user, Authentication authentication) {
        User temp = userRepository.findByUsername(authentication.getName());
        temp.setnDays(user.getnDays());
        userRepository.save(temp);
    }

    @PutMapping(value = "/setToken/{deviceToken}")
    public void setToken(@PathVariable String deviceToken,
                         Authentication authentication){
        String userName = authentication.getName();
        User user = userRepository.findByUsername(userName);
        user.setDeviceToken(deviceToken);

        userRepository.save(user);
    }

}

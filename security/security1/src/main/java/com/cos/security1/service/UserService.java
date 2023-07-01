package com.cos.security1.service;

import com.cos.security1.config.CustomBCryptPasswordEncoder;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final CustomBCryptPasswordEncoder encoder;
    private final UserRepository userRepository;

    @Transactional
    public void 회원가입(User user) {
        String rawPassword = user.getPassword();    // 1234 원문
        String encPassword = encoder.encode(rawPassword);   // 해쉬
        user.setPassword(encPassword);
        user.setRole("ROLE_USER");
        userRepository.save(user);
    }

    public User 회원찾기(String username) {
        User user = userRepository.findByUsername(username).orElseGet(() -> {
            return new User();
        });
        return user;
    }

}

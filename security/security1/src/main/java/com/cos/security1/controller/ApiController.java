package com.cos.security1.controller;

import com.cos.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApiController {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/auth/kakao/callback")
    public @ResponseBody String kakaoCallback(String code) {   // Data 를 리턴해주는 컨트롤러 함수
        return "카카오 인증 완료: 코드값 : " + code;
    }
}

package com.cos.security1.controller;

import com.cos.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
public class ApiController {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/auth/kakao/callback")
    public @ResponseBody String kakaoCallback(String code) {   // Data 를 리턴해주는 컨트롤러 함수

        // POST 방식으로 key=value 데이터를 요청 (카카오쪽으로)
        // 라이브러리
        // Retrofit2 - 안드로이드에서 많이 사용
        // OkHttp
        // RestTemplate

        RestTemplate restTemplate = new RestTemplate();

        // HttpHeader 오브젝트 생성
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");   // 내가 지금 전달할 데이터가 key=value 형태임을 알려주는 것.

        // HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code"); // 이 값들 다 변수화해서 사용하는 것이 좋긴 한데, 이해를 위해 하드코딩함.
        params.add("client_id", "f74062b59f442c72215d6ba27c33ac35");
        params.add("redirect_uri", "http://localhost:8080/auth/kakao/callback");
        params.add("code", code);

        // HttpHeader 와 HttpBody 를 하나의 HttpEntity 오브젝트에 담기 -> 이렇게 해주는 이유는, 아래의  restTemplate.exchange() 가 파라미터로 HttpEntity 를 받게 되있기 때문.
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, httpHeaders);

        // Http 요청하기 - Post 방식으로 - 그리고 reponse 변수로 응답받음
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",  // 토큰 발급 요청 주소
                HttpMethod.POST,    // 토큰 발급 요청 메서드는 카카오 문서상의 POST
                kakaoTokenRequest,  // HttpBody에 들어갈 데이터와, HttpHeader 값을 한번에 넣어줌
                String.class    // 응답받을 타입은 String 으로 지정
        );

        return "카카오 인증 완료: 토큰요청에 대한 응답 : " + response;
//        return "카카오 인증 완료: 토큰요청에 대한 응답 헤더: " + response.getHeaders();
//        return "카카오 인증 완료: 토큰요청에 대한 응답 바디: " + response.getBody();
    }
}

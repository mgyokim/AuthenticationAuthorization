package com.cos.security1.controller;

import com.cos.security1.model.KakaoProfile;
import com.cos.security1.model.OAuthToken;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import com.cos.security1.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ApiController {

    private final UserRepository userRepository;

    private final UserService userService;

//    @GetMapping("/auth/kakao/callback")
    public @ResponseBody String kakaoCallback_v1(String code) {   // Data 를 리턴해주는 컨트롤러 함수

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

        // Gson, Json Simple, ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oAuthToken = null;

        try {
            oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);  // Json 데이터를 자바로 처리하기 위해 자바 오브젝트로 바꿈.
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println("카카오 엑세스 토큰 : " + oAuthToken.getAccess_token());

        // ---------------------------------------------------------------------

        RestTemplate restTemplate2 = new RestTemplate();

        // HttpHeader 오브젝트 생성
        HttpHeaders httpHeaders2 = new HttpHeaders();
        httpHeaders2.add("Authorization", "Bearer " + oAuthToken.getAccess_token());
        httpHeaders2.add("Content-Type", "application/x-www-form-urlencoded");   // 내가 지금 전달할 데이터가 key=value 형태임을 알려주는 것.

        // HttpHeader 와 HttpBody 를 하나의 HttpEntity 오브젝트에 담기 -> 이렇게 해주는 이유는, 아래의  restTemplate.exchange() 가 파라미터로 HttpEntity 를 받게 되있기 때문.
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest2 =
                new HttpEntity<>(httpHeaders2);

        // Http 요청하기 - Post 방식으로 - 그리고 reponse 변수로 응답받음
        ResponseEntity<String> response2 = restTemplate2.exchange(
                "https://kapi.kakao.com/v2/user/me",  // 토큰 발급 요청 주소
                HttpMethod.POST,    // 토큰 발급 요청 메서드는 카카오 문서상의 POST
                kakaoProfileRequest2,  // HttpBody에 들어갈 데이터와, HttpHeader 값을 한번에 넣어줌
                String.class    // 응답받을 타입은 String 으로 지정
        );

        System.out.println(response2.getBody());

        // Gson, Json Simple, ObjectMapper
        ObjectMapper objectMapper2 = new ObjectMapper();
        KakaoProfile kakaoProfile = null;

        try {
            kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);  // Json 데이터를 자바로 처리하기 위해 자바 오브젝트로 바꿈.
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println("카카오 아이디(번호) : " + kakaoProfile.getId());
        System.out.println("카카오 이메일 : " + kakaoProfile.getKakao_account().getEmail());
        System.out.println("카카오 닉네임 : " + kakaoProfile.getProperties().getNickname());

//        return "카카오 인증 완료: 토큰요청에 대한 응답 : " + response;
//        return "카카오 인증 완료: 토큰요청에 대한 응답 헤더: " + response.getHeaders();
//        return "카카오 인증 완료: 토큰요청에 대한 응답 바디: " + response.getBody();
        return "카카오 사용자 정보 요청 완료 : " + response2.getBody();
    }

    /**
     * redirect uri 를 http://bookstore24.shop/auth/kakao/callback 로 변경함 (프론트 협업용)
     */

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
        params.add("redirect_uri", "http://bookstore24.shop/auth/kakao/callback");
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

        // Gson, Json Simple, ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oAuthToken = null;

        try {
            oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);  // Json 데이터를 자바로 처리하기 위해 자바 오브젝트로 바꿈.
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println("카카오 엑세스 토큰 : " + oAuthToken.getAccess_token());

        // ---------------------------------------------------------------------

        RestTemplate restTemplate2 = new RestTemplate();

        // HttpHeader 오브젝트 생성
        HttpHeaders httpHeaders2 = new HttpHeaders();
        httpHeaders2.add("Authorization", "Bearer " + oAuthToken.getAccess_token());
        httpHeaders2.add("Content-Type", "application/x-www-form-urlencoded");   // 내가 지금 전달할 데이터가 key=value 형태임을 알려주는 것.

        // HttpHeader 와 HttpBody 를 하나의 HttpEntity 오브젝트에 담기 -> 이렇게 해주는 이유는, 아래의  restTemplate.exchange() 가 파라미터로 HttpEntity 를 받게 되있기 때문.
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest2 =
                new HttpEntity<>(httpHeaders2);

        // Http 요청하기 - Post 방식으로 - 그리고 reponse 변수로 응답받음
        ResponseEntity<String> response2 = restTemplate2.exchange(
                "https://kapi.kakao.com/v2/user/me",  // 토큰 발급 요청 주소
                HttpMethod.POST,    // 토큰 발급 요청 메서드는 카카오 문서상의 POST
                kakaoProfileRequest2,  // HttpBody에 들어갈 데이터와, HttpHeader 값을 한번에 넣어줌
                String.class    // 응답받을 타입은 String 으로 지정
        );

        System.out.println(response2.getBody());

        // Gson, Json Simple, ObjectMapper
        ObjectMapper objectMapper2 = new ObjectMapper();
        KakaoProfile kakaoProfile = null;

        try {
            kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);  // Json 데이터를 자바로 처리하기 위해 자바 오브젝트로 바꿈.
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // User 오브젝트 : username, password, email
        System.out.println("카카오 아이디(번호) : " + kakaoProfile.getId());
        System.out.println("카카오 이메일 : " + kakaoProfile.getKakao_account().getEmail());

        System.out.println("서버에 저장할 유저네임은 : " + kakaoProfile.getKakao_account().getEmail() + "_" + kakaoProfile.getId());
        System.out.println("서버에 저장할 이메일 : " + kakaoProfile.getKakao_account().getEmail());
        UUID garbagePassword = UUID.randomUUID();
        System.out.println("서버에 저장할 패스워드 : " + garbagePassword);

        User kakaoUser = User.builder()
                .username(kakaoProfile.getKakao_account().getEmail() + "_" + kakaoProfile.getId())
                .password(garbagePassword.toString())
                .email(kakaoProfile.getKakao_account().getEmail())
                .build();

        // 비가입자만 체크 해서 회원가입 처리
        User originUser = userService.회원찾기(kakaoUser.getUsername());

        if (originUser == null) {
            userService.회원가입(kakaoUser);
        }

        // 로그인 처리 -> 65강 54분 부터






//        return "카카오 인증 완료: 토큰요청에 대한 응답 : " + response;
//        return "카카오 인증 완료: 토큰요청에 대한 응답 헤더: " + response.getHeaders();
//        return "카카오 인증 완료: 토큰요청에 대한 응답 바디: " + response.getBody();
        return "카카오 사용자 정보 요청 완료 : " + response2.getBody();
    }
}

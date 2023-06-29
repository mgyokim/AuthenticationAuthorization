package com.cos.security1.config.oauth;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.config.oauth.provider.FacebookUserInfo;
import com.cos.security1.config.oauth.provider.GoogleUserInfo;
import com.cos.security1.config.oauth.provider.NaverUserInfo;
import com.cos.security1.config.oauth.provider.OAuth2UserInfo;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    // Oauth 서버로부터 받은 userRequesst 데이터에 후처리되는 함수
    // 함수 죵료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    // 오버라이딩 하지 않아도 loadUser는 정상적으로 발동을 한다. 그러면 왜 오버라이딩을 해주엇느냐?
    // 1. OAuth 로그인한 회원을 강제 회원가입 시키기 위해.
    // 2. PrincipalDetails 타입으로 객체를 반환하기 위해.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest : " + userRequest.getClientRegistration()); //registrationId로 어떤 OAuth로 로그인 했는지 확인 가능.
        System.out.println("getAccessToken : " + userRequest.getAccessToken().getTokenValue());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 구글로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인을 완료 -> code를 리턴(OAuth2-Client 라이브러리가 받아줌) -> 해당 code를 통해서 AccessToken을 요청
        // userRequest 정보 -> 회원 프로필을 받아야함(loadUser 함수 호출) -> 구글로부터 회원 프로필 받아준다.
        System.out.println("getAttributes : " + oAuth2User.getAttributes());

        // 회원가입을 강제로 진행해볼 예정super.loadUser(userRequest).getAttributes() 값을 토대로.
        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
            System.out.println("페이스북 로그인 요청");
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
            System.out.println("네이버 로그인 요청");
        } else {
            System.out.println("우리는 구글과 페이스북고 네이버 로그인만 지원해요 ㅎㅎㅎ");
        }
        String provider = oAuth2UserInfo.getProvider();// google
        String providerId = oAuth2UserInfo.getProviderId(); // 105538402619290723967
        String username = provider + "_" + providerId;  // google_105538402619290723967
        String password = bCryptPasswordEncoder.encode("겟인데어");
        String email = oAuth2UserInfo.getEmail();   // absdf@gmail.com
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);// username이 일치하는 유저가 있는지.

        if (userEntity == null) {
            System.out.println(provider + " 로그인이 최초입니다.");
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        } else {
            System.out.println(provider + " 로그인을 이미 한적이 있습니다. 당신은 자동회원가입이 되어 있습니다.");
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}

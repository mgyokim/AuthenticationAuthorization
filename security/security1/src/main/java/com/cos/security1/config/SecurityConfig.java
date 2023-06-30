package com.cos.security1.config;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


//1. 코드받기(인증), 2. 엑세스토큰(권한), 3. 사용자프로필 정보를 가져오고 4-1. 그 정보를 토대로 회원가입을 자동으로 진행시키기도 함,
// 4-2. 정보가 좀 모자르면 ex) 구글(이메일 전화번호, 이름, 아이디) ,쇼핑몰(집주소), 백화점몰(vip등급, 일반등급) 이런게 필요하면, 자동회원가입시키는게아니라 추가적인 회원가입 창이 나와서 회원가입 해야한다.

@Configuration
@EnableWebSecurity  // 스프링 시큐리티 필터가 스프링 스프링 필터체인에 등록이 된다.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // secured 어노테이션 활성화, preAuthorize, postAuthorize 어노테이션 활성화
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;

    public SecurityConfig(PrincipalOauth2UserService principalOauth2UserService) {
        this.principalOauth2UserService = principalOauth2UserService;
    }

    /**
     * 순환 참조문제로 따로 클래스 만들어서 SecurityConfig -> PrincipalOauth2UserService -> CustomBCryptPasswordEncoder 로 구조를 변경함.
     */
//    // 해당 메서드의 리턴되는 오브젝트를 IOC로 등록해준다.
//    @Bean
//    public BCryptPasswordEncoder encodePwd() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()    // .antMatchers 는 스프링 시큐리티에서 경로에 대한 접근 권한을 설정하기 위해 사용
                .antMatchers("/user/**").authenticated()    // 해당 경로는 인증된 사용자만
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")   // 해당 경로는 어드민이거나 매니저만
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")   // 해당 경로는 어드민만
                .anyRequest().permitAll()  // 그 이외경로는 모두가 권한있음
                .and()
                .formLogin()
                .loginPage("/loginForm")   // 권한 없는 경로 접근시 이 경로로 강제이동
                .loginProcessingUrl("/login")  // /login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해줌. 그렇기때문에 컨트롤러에 따로 /login을 만들어주지 않아도 된다.
                .defaultSuccessUrl("/")    // 로그인 성공시 / 로(메인페이지) 보내줄건데, 만약, 너가 특정 페이지를 들어가려고 했었으면 그 페이지로 보내줄게.
                .and()
                .oauth2Login()
                .loginPage("/loginForm")
                .userInfoEndpoint()
                .userService(principalOauth2UserService); // OAuth 로그인 완료된 뒤의 후처리가 필요함. Tip. 코드X, (엑세스 토큰 + 사용자 프로필 정보)
        return http.build();
    }

}
// /user/** 로 들어오면 인증이 필수
package com.cos.security1.config.oauth.provider;

import java.util.Map;

// {id=1171131243, email=abcd@naver.com, name=김민교}
public class NaverUserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes; // oauth2User.getAttributes

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }


    public String getProviderId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}

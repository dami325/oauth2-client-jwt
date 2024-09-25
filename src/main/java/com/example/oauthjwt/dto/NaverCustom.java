package com.example.oauthjwt.dto;

import java.util.Map;

/**
 * 네이버 데이터 형식
 * {
 * 		resultcode=00, message=success, response={id=123123123, name=다미}
 * }
 */
public class NaverCustom implements OAuth2Custom {

    private final Map<String, Object> attribute;

    public NaverCustom(Map<String, Object> attribute) {

        this.attribute = (Map<String, Object>) attribute.get("response");
    }

    @Override
    public String getProvider() {

        return "naver";
    }

    @Override
    public String getProviderId() {

        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {

        return attribute.get("email").toString();
    }

    @Override
    public String getName() {

        return attribute.get("name").toString();
    }
}
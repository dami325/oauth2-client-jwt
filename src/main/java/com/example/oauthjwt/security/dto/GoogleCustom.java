package com.example.oauthjwt.security.dto;

import java.util.Map;

/**
 * 구글 데이터 형식
 * {
 * 		resultcode=00, message=success, id=123123123, name=다미
 * }
 */
public class GoogleCustom implements OAuth2Custom {

    private final Map<String, Object> attribute;

    public GoogleCustom(Map<String, Object> attribute) {

        this.attribute = attribute;
    }

    @Override
    public String getProvider() {

        return "google";
    }

    @Override
    public String getProviderId() {

        return attribute.get("sub").toString();
    }

    @Override
    public String getEmail() {

        return attribute.get("email").toString();
    }

    @Override
    public String getOAuthName() {

        return attribute.get("name").toString();
    }
}
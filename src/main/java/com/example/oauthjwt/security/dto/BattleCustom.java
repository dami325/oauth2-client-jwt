package com.example.oauthjwt.security.dto;

import java.util.Map;

/**
 * 배틀넷 attribute 형태
 * {
 * 		sub=00000000, id=00000000, battletag=다미#31630
 * }
 */
public class BattleCustom implements OAuth2Custom {

    private final Map<String, Object> attribute;

    public BattleCustom(Map<String, Object> attribute) {

        this.attribute = attribute;
    }

    @Override
    public String getProvider() {

        return "battle";
    }

    @Override
    public String getProviderId() {

        return attribute.get("sub").toString();
    }

    @Override
    public String getEmail() {

        return null;
    }

    @Override
    public String getOAuthName() {

        return attribute.get("battletag").toString();
    }
}
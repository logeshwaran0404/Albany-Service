package com.albany.vsm.security;

import com.albany.vsm.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;


import java.util.Map;

class CustomOidcUserPrincipal implements OidcUser {
    
    private final OidcUser oidcUser;
    private final CustomUserPrincipal customUserPrincipal;
    
    public CustomOidcUserPrincipal(User user, OidcUser oidcUser) {
        this.oidcUser = oidcUser;
        this.customUserPrincipal = new CustomUserPrincipal(user, oidcUser.getAttributes());
    }
    
    public CustomUserPrincipal getCustomUserPrincipal() {
        return customUserPrincipal;
    }
    
    @Override
    public Map<String, Object> getClaims() {
        return oidcUser.getClaims();
    }
    
    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUser.getUserInfo();
    }
    
    @Override
    public OidcIdToken getIdToken() {
        return oidcUser.getIdToken();
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return customUserPrincipal.getAttributes();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return customUserPrincipal.getAuthorities();
    }
    
    @Override
    public String getName() {
        return customUserPrincipal.getName();
    }
}
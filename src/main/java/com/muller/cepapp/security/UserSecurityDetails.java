package com.muller.cepapp.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class UserSecurityDetails extends org.springframework.security.core.userdetails.User {

    private Long id;

    public UserSecurityDetails(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    
}

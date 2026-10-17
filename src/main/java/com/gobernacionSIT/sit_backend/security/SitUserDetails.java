package com.gobernacionSIT.sit_backend.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class SitUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean primerIngreso;

    public SitUserDetails(
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            boolean primerIngreso) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.primerIngreso = primerIngreso;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public boolean isPrimerIngreso() {
        return primerIngreso;
    }
}

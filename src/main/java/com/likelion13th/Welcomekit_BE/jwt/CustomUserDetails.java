package com.likelion13th.Welcomekit_BE.jwt;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.likelion13th.Welcomekit_BE.domain.User;

public class CustomUserDetails implements UserDetails {

	private final String email;
	private final String password;
	private final Collection<? extends GrantedAuthority> authorities;

	public CustomUserDetails(User user) {
		this.password = user.getPassword();
		this.email = user.getEmail();
		// UserType(BABY_LION, ADMIN)을 스프링 시큐리티 권한으로 매핑
		this.authorities = Collections.singletonList(
			new SimpleGrantedAuthority("ROLE_" + user.getUserType().name()));
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
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}

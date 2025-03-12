package com.likelion13th.Welcomekit_BE.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.jwt.CustomUserDetails;
import com.likelion13th.Welcomekit_BE.service.CustomUserDetailsService;

@Service
public class CustomUserDetailsManager implements UserDetailsService {

	@Autowired
	CustomUserDetailsService customUserDetailsService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = customUserDetailsService.findByUserName(username);
		if (user != null) {
			return new CustomUserDetails(user);
		}
		throw new UsernameNotFoundException("User not found with username: ");
	}
}

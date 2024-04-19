package com.example.demo.models.security;

import com.example.demo.models.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class UserAuthService  implements UserDetailsService {
  @Autowired
  private UserRepository userRepository;

  @Transactional(readOnly = true)
  @Override
  public UserDetails loadUserByUsername(String username) {
    return userRepository.findByUsername(username)
        .map(user ->
            User.withUsername(username)
                .authorities(user.getRoles().stream()
                    .map(Enum::name)
                    .map(SimpleGrantedAuthority::new)
                    .toList()
                )
                .password(user.getPassword())
                .build())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }
}

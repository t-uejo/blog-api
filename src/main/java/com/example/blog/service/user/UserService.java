package com.example.blog.service.user;

import com.example.blog.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(String username, String rawPassword){
        String encodedPassword = passwordEncoder.encode(rawPassword);
        userRepository.insert(username, encodedPassword, true);
    }

    @Transactional
    public void delete(String username){
        userRepository.deleteByUsername(username);
    }
}

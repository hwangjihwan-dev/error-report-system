package com.hwang.errorreport.service;

import com.hwang.errorreport.domain.user.User;
import com.hwang.errorreport.dto.auth.SignupRequest;
import com.hwang.errorreport.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(SignupRequest request) {
        if(userRepository.existsByLoginId(request.getPassword())){
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getLoginId(),
                encodedPassword,
                request.getName()
        );

        userRepository.save(user);

    }
}

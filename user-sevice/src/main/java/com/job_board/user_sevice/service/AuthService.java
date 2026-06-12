package com.job_board.user_sevice.service;

import com.job_board.user_sevice.dto.AuthResponse;
import com.job_board.user_sevice.dto.LoginRequest;
import com.job_board.user_sevice.dto.RegisterRequest;
import com.job_board.user_sevice.dto.UserDto;
import com.job_board.user_sevice.entity.User;
import com.job_board.user_sevice.repository.UserRepo;
import com.job_board.user_sevice.util.jwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    public final jwtUtil jwtUtil;
    public final UserRepo userRepo;
    public final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request){

        if(userRepo.existsByEmail(request.getEmail())){
            throw new RuntimeException("E-Mail already registered" + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(request.getRole())
                .build();
        User savedUser = userRepo.save(user);
        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole().name(), savedUser.getId());

        return AuthResponse.builder()
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole().name())
                .token(token)
                .expiresIn(86400000L)
                .build();

    }

    public AuthResponse login(LoginRequest request){
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(()-> new BadCredentialsException("Invalid Email"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new BadCredentialsException("Password doesn't Match");
        }

        String token = jwtUtil.generateToken(user.getEmail(),user.getRole().name(), user.getId());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .fullName(user.getFullName())
                .expiresIn(86400000L)
                .build();
    }

    public UserDto getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }
}

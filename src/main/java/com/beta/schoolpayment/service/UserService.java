package com.beta.schoolpayment.service;

import com.beta.schoolpayment.dto.request.AuthRequest;
import com.beta.schoolpayment.dto.request.UserRequest;
import com.beta.schoolpayment.dto.response.AuthResponse;
import com.beta.schoolpayment.dto.response.UserResponse;
import com.beta.schoolpayment.exception.DataNotFoundException;
import com.beta.schoolpayment.exception.ValidationException;
import com.beta.schoolpayment.model.User;
import com.beta.schoolpayment.repository.StudentRepository;
import com.beta.schoolpayment.repository.UserRepository;
import com.beta.schoolpayment.security.CustomUserDetails;
import com.beta.schoolpayment.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository =userRepository;
        this.passwordEncoder=new BCryptPasswordEncoder();
    }
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return userRepository.findByEmailOrNis(usernameOrEmail, convertNis(usernameOrEmail))
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getEmail(), // Tetap gunakan email sebagai username utama
                        user.getPassword(),
                        new ArrayList<>()
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Method tambahan untuk konversi NIS jika memungkinkan
    private Long convertNis(String input) {
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            return null; // Jika bukan angka, berarti ini email
        }
    }


    //register user
    @Transactional
    public UserResponse register(UserRequest userRequest) {
            if (userRepository.findUserByNis(userRequest.getNis()).isPresent()) {
                throw new IllegalArgumentException("NIS already exists");
            }
            if (!studentRepository.existsByNis(userRequest.getNis())) {
                throw new IllegalArgumentException("NIS does not exist");
            }
            if (userRepository.findUserByEmail(userRequest.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }

            if (userRequest.getPassword().length() < 8) {
                throw new ValidationException("Password must be at least 8 characters");
            }
            if (!userRequest.getPassword().equals(userRequest.getConfirmPassword())) {
                throw new ValidationException("Password and confirm password do not match");
            }


            String encryptedPassword = passwordEncoder.encode(userRequest.getPassword());

            User user = new User();
            user.setNis(userRequest.getNis());
            user.setName(userRequest.getName());
            user.setEmail(userRequest.getEmail());
            user.setPassword(encryptedPassword);
            user.setRole("STUDENT");

            User savedUser = userRepository.save(user);

            return convertToResponse(savedUser);
    }
    //login
    public AuthResponse login(AuthRequest authRequest) {
        String identifier = authRequest.getEmail(); // Bisa jadi Email atau NIS dalam bentuk String
        Long nis = convertNis(identifier);
        // Autentikasi menggunakan UsernamePasswordAuthenticationToken
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(identifier, authRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            // Cari user berdasarkan Email atau NIS
            User user = userRepository.findByEmailOrNis(identifier, nis)
                    .orElseThrow(() -> new DataNotFoundException("User not found"));

            // Generate token JWT
            String token = jwtUtil.generateToken(user);
            return new AuthResponse(token);
        } else {
            throw new RuntimeException("Invalid authentication");
        }
    }


    public UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setNis(user.getNis());
        response.setUserId(user.getUserId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }


}

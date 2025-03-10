package com.beta.schoolpayment.service;

import com.beta.schoolpayment.dto.request.UserRequest;
import com.beta.schoolpayment.dto.response.UserResponse;
import com.beta.schoolpayment.exception.ValidationException;
import com.beta.schoolpayment.model.User;
import com.beta.schoolpayment.repository.StudentRepository;
import com.beta.schoolpayment.repository.UserRepository;
import com.beta.schoolpayment.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    @Lazy
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository =userRepository;
        this.passwordEncoder=new BCryptPasswordEncoder();
    }
    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        return userRepository.findByName(name)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with name: " + name));
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

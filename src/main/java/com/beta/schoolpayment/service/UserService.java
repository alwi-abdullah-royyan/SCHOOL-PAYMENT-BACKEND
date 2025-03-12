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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    private final StudentRepository studentRepository;

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String imageDirectory="src/main/resources/static/images";
    private static final String[] allowedFileTypes = {"image/jpeg", "image/png", "image/jpg"};

    @Autowired
    public UserService(UserRepository userRepository, StudentRepository studentRepository) {
        this.userRepository =userRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder=new BCryptPasswordEncoder();
    }
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrNis(usernameOrEmail, convertNis(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }

    // Method tambahan untuk konversi NIS jika memungkinkan
    public Long convertNis(String input) {
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
        String identifier = authRequest.getIdentifier();
        Long nis = convertNis(identifier); // Cek apakah identifier ini NIS

        // Cari user berdasarkan Email atau NIS
        User user = userRepository.findByEmailOrNis(identifier, nis)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        // Gunakan email user untuk autentikasi karena Spring Security mengenal email sebagai username
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), authRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            // Generate token JWT
            String token = jwtUtil.generateToken(user);
            return new AuthResponse(token);
        } else {
            throw new RuntimeException("Invalid authentication");
        }
    }

    public Page<UserResponse> getAllUser(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> user = userRepository.findAll(pageable);
        return user.map(this::convertToResponse);
    }

    public Page<UserResponse> getUserFilter(int page, int size, String role) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findByRoleOrderByUpdatedAtDesc(role, pageable);
        return userPage.map(this::convertToResponse);
    }

    @Transactional
    public UserResponse updateRole(UUID userId, UserRequest userRequest) {
        if (userRequest.getRole() == null) {
            throw new IllegalArgumentException("Role cannot be null");
        } else if (!userRequest.getRole().equals("STUDENT") && !userRequest.getRole().equals("ADMIN")) {
            throw new IllegalArgumentException("Role must be 'STUDENT' or 'ADMIN'");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User not found"));
        user.setRole(userRequest.getRole());
        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }
    @Transactional
    public UserResponse softDelete(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User not found"));
        user.setDeletedAt(LocalDateTime.now());
        User deletedUser = userRepository.save(user);
        return convertToResponse(deletedUser);
    }
    @Transactional
    public void hardDelete(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User not found"));
        userRepository.delete(user);
    }
    //Validasi file
    public static void validateFile(MultipartFile file){
        long maxFileSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxFileSize) {
            throw new ValidationException("File size must be less than 5MB");
        }
        String fileType=file.getContentType();
        boolean isValidType=false;
        for (String type : allowedFileTypes){
            if (Objects.equals(type, fileType)){
                isValidType=true;
                break;
            }
        }
        if (!isValidType){
            throw new ValidationException("File type must be image/jpeg, image/png, or image/jpg");
        }
    }
    private String generateUniqueFileName(String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }
    @Transactional
    public UserResponse updateProfile(Authentication authentication, UserRequest userRequest) {
        UserDetails auth = (UserDetails) authentication.getPrincipal();
        String username = auth.getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new DataNotFoundException("User not found"));
        if (userRequest.getEmail() != null) {
            if (userRepository.findUserByEmail(userRequest.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(userRequest.getEmail());
        }
        if (userRequest.getPassword() != null) {
            if (userRequest.getPassword().length() <= 8) {
                throw new ValidationException("Password must be at least 8 characters");
            }
            if (!userRequest.getPassword().equals(userRequest.getConfirmPassword())){
                throw new ValidationException("Password and confirm password do not match");
            }
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        if(userRequest.getProfilePicture() != null && !userRequest.getProfilePicture().isEmpty()) {
            System.out.println(imageDirectory);
            MultipartFile file = userRequest.getProfilePicture();
            validateFile(file);
            String uniqueFileName = generateUniqueFileName(Objects.requireNonNull(file.getOriginalFilename()));
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path fullPath = Path.of(imageDirectory, datePath, uniqueFileName);
            System.out.println("File original name: " + fullPath);

            Path imagePath = Path.of(datePath, uniqueFileName);

            try {
                Files.createDirectories(fullPath.getParent());
                Files.copy(file.getInputStream(), fullPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            user.setProfilePicture(imagePath.toString());
        }
        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }

    public byte[] getImageById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        Path imagePath = Path.of(imageDirectory, user.getProfilePicture());
        System.out.println(imagePath);
        if (!Files.exists(imagePath)) {
            throw new DataNotFoundException("Profile picture not found for user: " + userId);
        }

        try {
            return Files.readAllBytes(imagePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read profile picture for user: " + userId, e);
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
        response.setDeletedAt(user.getDeletedAt());
        return response;
    }


}

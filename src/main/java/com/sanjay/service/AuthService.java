package com.sanjay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sanjay.config.JwtUtil;
import com.sanjay.dto.LoginRequest;
import com.sanjay.dto.LoginResponse;
import com.sanjay.dto.RegisterResponse;
import com.sanjay.dto.RegistrationRequest;
import com.sanjay.dto.Role;
import com.sanjay.entity.Customer;
import com.sanjay.entity.User;
import com.sanjay.exception.QuickRentalException;
import com.sanjay.repository.CustomerRepository;
import com.sanjay.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public RegisterResponse register(RegistrationRequest request) {

        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getDrivingLicenceNo() != null) {

            Customer c = new Customer();
            c.setCustomerName(request.getUsername());
            c.setDrivingLicenceNo(request.getDrivingLicenceNo());
            c.setAge(request.getAge());

            Customer saved = customerRepository.save(c);

            user.setRole(Role.CUSTOMER);
            user.setCustomer(saved);

        } else {
            user.setRole(Role.AGENT);
        }

        userRepository.save(user);

        // Generate JWT token after registration
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        Integer customerId = (user.getCustomer() != null) ? user.getCustomer().getCustomerId() : null;

        return new RegisterResponse(
                "Registered Successfully as " + user.getRole(),
                token,
                user.getRole(),
                customerId
        );
    }


    public LoginResponse login(LoginRequest req) throws QuickRentalException {

        User user = userRepository.findByUsername(req.getUsername());
        if (user == null) {
            throw new QuickRentalException("Invalid username or password");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new QuickRentalException("Invalid username or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        Integer customerId = (user.getCustomer() != null) ? user.getCustomer().getCustomerId() : null;

        return new LoginResponse(
                "Login successful",
                token,
                user.getRole(),
                customerId
        );
    }
}


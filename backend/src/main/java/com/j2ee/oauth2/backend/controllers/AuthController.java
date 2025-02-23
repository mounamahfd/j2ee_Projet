package com.j2ee.oauth2.backend.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.j2ee.oauth2.backend.dtos.TokenDto;
import com.j2ee.oauth2.backend.dtos.UrlDto;
import com.j2ee.oauth2.backend.models.User;
import com.j2ee.oauth2.backend.payload.request.RoleUpdateRequest;
import com.j2ee.oauth2.backend.repository.RoleRepository;
import com.j2ee.oauth2.backend.repository.UserRepository;
import com.j2ee.oauth2.backend.security.Exception.ResourceAlreadyExistsException;
import com.j2ee.oauth2.backend.services.UserDetailsImpl;
import com.j2ee.oauth2.backend.models.ERole;
import com.j2ee.oauth2.backend.models.Role;
import com.j2ee.oauth2.backend.payload.request.LoginRequest;
import com.j2ee.oauth2.backend.payload.request.SignupRequest;
import com.j2ee.oauth2.backend.payload.response.JwtResponse;
import com.j2ee.oauth2.backend.payload.response.MessageResponse;
import com.j2ee.oauth2.backend.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @Value("${spring.security.oauth2.resourceserver.opaque-token.clientId}")
    private String clientId;

    @Value("${spring.security.oauth2.resourceserver.opaque-token.clientSecret}")
    private String clientSecret;



    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthenticationManager authenticationManager;


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        System.out.println("Signup Request: " + signUpRequest);
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new ResourceAlreadyExistsException("Erreur : Le nom d'utilisateur est déjà pris !");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("Erreur : L'adresse e-mail est déjà utilisée !");
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        // Set default active value
        user.setActive(true);

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_AGENT)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "dir":
                        Role dirRole = roleRepository.findByName(ERole.ROLE_DIRECTEUR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(dirRole);
                        break;
                    case "vend":
                        Role vendRole = roleRepository.findByName(ERole.ROLE_VENDEUR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(vendRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_AGENT)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });

        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }


    @GetMapping("/url")
    public ResponseEntity<UrlDto> auth() {
        String url = new GoogleAuthorizationCodeRequestUrl(clientId,
                "http://localhost:4200",
                Arrays.asList(
                        "email",
                        "profile",
                        "openid")).build();
        return ResponseEntity.ok(new UrlDto(url));
    }

    @GetMapping("/callback")
    public ResponseEntity<TokenDto> callback(@RequestParam("code") String code) throws URISyntaxException {

        String token;
        try {
            token = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(), new GsonFactory(),
                    clientId,
                    clientSecret,
                    code,
                    "http://localhost:4200"
            ).execute().getAccessToken();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(new TokenDto(token));
    }


    //  // Get all users (Admin only)
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }





    }

    @PutMapping("/users/{id}/roles")
    @Transactional
    public ResponseEntity<?> updateUserRoles(@PathVariable("id") Long id, @Valid @RequestBody RoleUpdateRequest roleUpdateRequest) {
        logger.info("Received request to update roles for user with ID: {}", id);

        Optional<User> userData = userRepository.findById(id);
        if (!userData.isPresent()) {
            logger.error("User with ID: {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Error: User not found!"));
        }

        User user = userData.get();

        logger.info("Current roles for user {}: {}", user.getId(), user.getRoles());

        Set<String> strRoles = roleUpdateRequest.getRole();
        Set<Role> roles = new HashSet<>();
        assignRoles(strRoles, roles);

        user.setRoles(roles);
        userRepository.save(user);

        logger.info("Updated roles for user {}: {}", user.getId(), user.getRoles());

        return ResponseEntity.ok(new MessageResponse("Roles updated successfully!"));
    }

    private void assignRoles(Set<String> strRoles, Set<Role> roles) {
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_AGENT)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "dir":
                        Role drRole = roleRepository.findByName(ERole.ROLE_DIRECTEUR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(drRole);
                        break;
                    case "agent":
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_AGENT)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        // Log the roles being assigned for debugging
        logger.info("Roles assigned: {}", roles);
    }


    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Error: Could not delete user!"));
        }
    }



    @GetMapping("/userinfo")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        String jwt = jwtUtils.parseJwt(request);

        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            String username = jwtUtils.getUserNameFromJwtToken(jwt);
            return ResponseEntity.ok(username);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }

    @PutMapping("/users/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        Optional<User> userData = userRepository.findById(id);

        if (userData.isPresent()) {
            User user = userData.get();
            user.setActive(true);
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse("User activated successfully!"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Error: User not found!"));
        }
    }

    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        Optional<User> userData = userRepository.findById(id);

        if (userData.isPresent()) {
            User user = userData.get();
            user.setActive(false);
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse("User deactivated successfully!"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Error: User not found!"));
        }
    }



}

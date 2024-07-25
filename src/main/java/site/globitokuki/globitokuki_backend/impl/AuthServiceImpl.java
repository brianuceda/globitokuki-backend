package site.globitokuki.globitokuki_backend.impl;

import org.springframework.stereotype.Service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.AuthenticationException;

import site.globitokuki.globitokuki_backend.dtos.AuthRequestDTO;
import site.globitokuki.globitokuki_backend.dtos.AuthResponseDTO;
import site.globitokuki.globitokuki_backend.dtos.ResponseDTO;
import site.globitokuki.globitokuki_backend.entity.RoleEnum;
import site.globitokuki.globitokuki_backend.entity.UserEntity;
import site.globitokuki.globitokuki_backend.repositories.UserRepository;
import site.globitokuki.globitokuki_backend.services.AuthService;
import site.globitokuki.globitokuki_backend.utils.JwtUtils;

@Service
public class AuthServiceImpl implements AuthService {
  private AuthenticationManager authenticationManager;
  private UserDetailsService userDetailsService;
  private UserRepository userRepository;
  private JwtUtils jwtUtils;
  private PasswordEncoder passwordEncoder;

  public AuthServiceImpl(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
      UserRepository userRepository, JwtUtils jwtUtils,
      PasswordEncoder passwordEncoder) {
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
    this.userRepository = userRepository;
    this.jwtUtils = jwtUtils;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public ResponseDTO login(AuthRequestDTO requestBody) {
    try {
      authenticationManager
          .authenticate(new UsernamePasswordAuthenticationToken(requestBody.getUsername(), requestBody.getPassword()));
      UserDetails userDetails = userRepository.findByUsername(requestBody.getUsername()).get();
      String token = jwtUtils.genToken(userDetails);
      return new AuthResponseDTO("Sesión iniciada correctamente.", 200, token);
    } catch (AuthenticationException ex) {
      return new ResponseDTO("Credenciales incorrectos.", 401);
    }
  }
  
  @Override
  public ResponseDTO register(AuthRequestDTO requestBody) {
    if (userRepository.findByUsername(requestBody.getUsername()).isPresent()) {
      return new ResponseDTO("El usuario ya existe.", 401);
    }

    UserEntity user = UserEntity.builder()
        .username(requestBody.getUsername())
        .password(passwordEncoder.encode(requestBody.getPassword()))
        .role(RoleEnum.ADMIN)
        .build();

    userRepository.save(user);
    String token = jwtUtils.genToken(user);

    return new AuthResponseDTO("Usuario creado correctamente.", 200, token);
  }

  @Override
  public ResponseDTO verify(AuthResponseDTO requestBody) {
    try {
      String username = jwtUtils.getUsernameFromToken(requestBody.getToken());
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      
      if (jwtUtils.isTokenValid(requestBody.getToken(), userDetails)) {
        return new ResponseDTO(true, 200);
      } else {
        return new ResponseDTO(false, 200);
      }
    } catch (Exception e) {
      return new ResponseDTO(false, 200);
    }

  }
}

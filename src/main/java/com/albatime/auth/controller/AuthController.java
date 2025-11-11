package com.albatime.auth.controller;

import com.albatime.auth.dto.LoginRequestDto;
import com.albatime.auth.dto.LoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequest, HttpServletRequest request) {
        try {
            log.info("로그인 시도: {}", loginRequest.getUsername());

            // 사용자 정보 조회
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

            // 비밀번호 검증
            if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
                log.warn("로그인 실패: 잘못된 비밀번호 - {}", loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(LoginResponseDto.builder()
                        .message("아이디 또는 비밀번호가 올바르지 않습니다.")
                        .authenticated(false)
                        .build());
            }

            // 인증 객체 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );

            // SecurityContext에 인증 정보 저장
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            // 세션에 SecurityContext 저장
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

            log.info("로그인 성공: {}", loginRequest.getUsername());

            return ResponseEntity.ok(LoginResponseDto.builder()
                .username(userDetails.getUsername())
                .message("로그인 성공")
                .authenticated(true)
                .build());

        } catch (BadCredentialsException e) {
            log.warn("로그인 실패: 사용자를 찾을 수 없음 - {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(LoginResponseDto.builder()
                    .message("아이디 또는 비밀번호가 올바르지 않습니다.")
                    .authenticated(false)
                    .build());
        } catch (Exception e) {
            log.error("로그인 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(LoginResponseDto.builder()
                    .message("로그인 처리 중 오류가 발생했습니다.")
                    .authenticated(false)
                    .build());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LoginResponseDto> logout(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            SecurityContextHolder.clearContext();

            log.info("로그아웃 성공");

            return ResponseEntity.ok(LoginResponseDto.builder()
                .message("로그아웃 되었습니다.")
                .authenticated(false)
                .build());
        } catch (Exception e) {
            log.error("로그아웃 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(LoginResponseDto.builder()
                    .message("로그아웃 처리 중 오류가 발생했습니다.")
                    .authenticated(false)
                    .build());
        }
    }

    @GetMapping("/check")
    public ResponseEntity<LoginResponseDto> checkAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.ok(LoginResponseDto.builder()
                .username(authentication.getName())
                .message("인증됨")
                .authenticated(true)
                .build());
        }

        return ResponseEntity.ok(LoginResponseDto.builder()
            .message("인증되지 않음")
            .authenticated(false)
            .build());
    }
}
package com.example.foodthought.service;

import com.example.foodthought.common.dto.ResponseDto;
import com.example.foodthought.dto.auth.LoginUserDto;
import com.example.foodthought.security.jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    public ResponseDto login(LoginUserDto loginUserDto,
                             HttpServletResponse response){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserDto.getUserId(),loginUserDto.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        List<String> userRols = new ArrayList<>();
        for (GrantedAuthority authority : authorities) {
            String userRole = authority.getAuthority();
            userRols.add(userRole);
        }

        String token = jwtUtil.createToken(userDetails.getUsername(),userRols).split(" ")[1];
        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER,token);
        cookie.setMaxAge((int)jwtUtil.TOKEN_TIME);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseDto.success(HttpStatus.OK.value());
    }
    public ResponseDto logout(HttpServletRequest request,
                              HttpServletResponse response){

        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER,null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseDto.success(HttpStatus.OK.value());
    }
}

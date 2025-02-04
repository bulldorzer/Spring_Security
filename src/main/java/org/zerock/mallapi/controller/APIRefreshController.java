package org.zerock.mallapi.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.zerock.mallapi.util.CustomJWTException;
import org.zerock.mallapi.util.JWTUtil;

import java.util.Date;
import java.util.Map;

@RestController // 값을 받는 컨트롤러
@RequiredArgsConstructor
@Log4j2
public class APIRefreshController {

    // JWT 토큰을 갱신(Refresh)
    /*
        코드의 주요 역할
        - access token이 유효한지 확인
        - 만료되었다면 Refresh Token(리프레시 토큰) 검증하여 새로운 Access Token 발급
        - Refresh Token의 남은 시간 1시간 이하라면 새 Refresh Token 발급
    */
    
    
    @RequestMapping("/api/member/refresh")
    public Map<String, Object> refresh(@RequestHeader("Authorization")String authHeader, String refreshToken){
        // HTTP 헤더에서 AccessToken 값 가져오기
        
        // RefreshToken이 없으면 예외 발생
        if (refreshToken == null){
            throw new CustomJWTException("NULL_REFRASH");
        }

        // RefreshToken이 제대로 전달되지 않았으면 예외 발생
        if (authHeader == null || authHeader.length() < 7){
            throw new CustomJWTException("INVALID_STRING");
        }

        // Token값 추출
        String accessToken = authHeader.substring(7);

        // Access Token 이 만료되지 않았다면 - Map형태로 토큰 반환
        // checkExpiredToken => 만료시간 체크하는 메서드
        if (checkExpiredToken(accessToken) == false){
            return Map.of("accessToken",accessToken,"refreshToken",refreshToken);
        }

        // Refresh토큰 검증 - 토큰 정보 반환
        Map<String ,Object> claims = JWTUtil.validateToken(refreshToken);

        log.info("refresh .. ciaims: "+claims);

        // 새로운 AccessToken 발급
        String newAccessToken = JWTUtil.generateToken(claims,10); // 사용자 정보 + 10분

        // 새로운 RefreshToken 발급
        String newRefreshToken = checkTime((Integer)claims.get("exp")) == true // 토큰 유효시간 60분 미만인가? 
                ? JWTUtil.generateToken(claims,60*24) //   true면 새로운토큰 발급
                : refreshToken; // 아니면 기존꺼 반환

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }

    // 시간이 1시간 미만으로 남았다면
    private boolean checkTime(Integer exp) {

        // JWT exp를 날짜로 변환
        Date expDate = new Date((long)exp * 1000 );

        // 현재 시간과의 차이 계산 - 밀리 세컨즈
        long gap = expDate.getTime() - System.currentTimeMillis();

        // 분단위 계산
        long leftmin = gap / (1000*60);

        // 1시간도 안남았는지..
        return leftmin < 60;
    }

    private boolean checkExpiredToken(String token){

        try {
            JWTUtil.validateToken(token); // 토큰이 유효한가?
        } catch (CustomJWTException ex) {
            // 토큰이 유효시간이 만료되었으면 true 반환
            if (ex.getMessage().equals("Expired")){
                return true;
            }
        }

        return false;
    }

}

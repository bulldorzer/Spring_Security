package org.zerock.mallapi.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

public class JWTUtil {

    // p324

    // key값 설정
    // 30글자이상 문자열 ( 하드코딩 X , 별도 설정파일에서 관리하는게 좋음)
    private static String key ="1234567890123456789012345678901234567890";

    // JWT문자열 생성을 위한 generateToken()
    public static String generateToken(Map<String, Object> valueMap, int min){
        SecretKey key = null;
        try {
            // HMAC-SHA 알고리즘 - 서명할 비밀키 생성
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

        String jwtStr = Jwts.builder()
                .setHeader(Map.of("typ","JWT")) // 헤더 설정 type : JWT
                .setClaims(valueMap) // 사용자 데이터 설정
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant())) // 발행시간
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant())) // 만료시간
                .signWith(key) // 비밀키 서명 = HMAC-SHA 알고리즘으로 서명(=토큰이 변조되지 않도록 증명하는 역할)
                .compact(); // JWT 문자열 생성

        return jwtStr;
    }

    public static Map<String, Object> validateToken(String token) {
        Map<String, Object> claim = null;
        try{
            SecretKey key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8")); // 검증용 동일한 비밀키 생성
            claim = Jwts.parserBuilder()
                    .setSigningKey(key) // 서명키 설정
                    .build() // JWT 파서 빌드
                    .parseClaimsJws(token) // JWT 파싱 및 검증, 실패 시 에러
                    .getBody();// 검증된 클래임(username, password) 반환
        }catch(MalformedJwtException malformedJwtException){
            throw new CustomJWTException("MalFormed"); // JWT 형식이 올바르지 안흠
        }catch(ExpiredJwtException expiredJwtException){
            throw new CustomJWTException("Expired"); // 토큰 만료
        }catch(InvalidClaimException invalidClaimException){
            throw new CustomJWTException("Invalid"); // 클래임 정보 올바르지 않음
        }catch(JwtException jwtException){
            throw new CustomJWTException("JWTError");// 기타 JWT 관련에러
        }catch(Exception e){
            throw new CustomJWTException("Error"); // 알수없는 에러
        }
        return claim;

    }
}

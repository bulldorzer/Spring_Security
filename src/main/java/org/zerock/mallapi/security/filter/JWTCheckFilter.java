package org.zerock.mallapi.security.filter;

import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zerock.mallapi.dto.MemberDTO;
import org.zerock.mallapi.util.JWTUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Log4j2
public class JWTCheckFilter extends OncePerRequestFilter {

    // 특정 경로는 필터 예외처리
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        // OPTIONS 메서드는 필터 거치치 않도록 설정
        if (request.getMethod().equals("OPTIONS")) return true;

        String path = request.getRequestURI();
        // 특정경로 (API호출, 이미지 조회 등) 필터 거치치 않도록 설정
        if (path.startsWith("/api/member") || path.startsWith("/api/products/view")){
            return true;
        }

        return false;
    }
    
    // 인증, 검증 및 예외처리
    /*
        헤더의 정보
        POST /api/member/login
        Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
        
        403에러는(토큰 유효시간 만료) doFilterInternal에서발생함
    */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {

        log.info("------------------JWTCheckFilter 실행---------------");
        String authHeaderStr = request.getHeader("Authorization");

        try {
            // "Bearer <token>" 에서 7번째 문자 이후로 잘라오기 =  실제 토큰 부분 추출
            String accessToken = authHeaderStr.substring(7);

            // JWT 검증 및 claims 추출
            Map<String, Object> claims = JWTUtil.validateToken(accessToken); // 유효성 검사 비밀키로 확인
            log.info("JWT claims: "+claims);

//            filterChain.doFilter(request, response); // 검증 성공시 다음 필터로 전달

            // 사용자 정보 추출
            String email = (String) claims.get("email");
            String pw = (String) claims.get("pw");
            String nickname = (String) claims.get("nickname");
            Boolean social = (Boolean) claims.get("social");
            List<String> roleNames = (List<String>) claims.get("roleNames");

            MemberDTO memberDTO = new MemberDTO(email,pw,nickname,social.booleanValue(),roleNames);

            log.info("====================================");
            log.info(memberDTO);
            log.info(memberDTO.getAuthorities());

            // Spring Security 인증 객체 설정 - 사용자를 수동으로 인증 시키는 역할
            // 사용자 정보, 계정 비밀번호, 계정 권한
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(memberDTO,pw,memberDTO.getAuthorities());

            // 시큐리티 컨텍스 객체 인증된 사용자 정보 저장
            // 접근할 때 SecurityContextHolder.getContext()를 통해서 접근한다
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request,response);

        } catch (Exception e) {
            log.error("JWT Check Error: "+e.getMessage());

            Gson gson = new Gson();
            String msg = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN")); // 유효기간 오류 문제 부분

            response.setContentType("application/json");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(msg);
            printWriter.close();
        }
    }
}

package org.zerock.mallapi.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.zerock.mallapi.dto.MemberDTO;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;



@Log4j2
public class APILoginSuccessHandler implements AuthenticationSuccessHandler  {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication)
        throws IOException, ServletException{

        log.info("================================================");
        log.info(authentication);
        log.info("================================================");

        // p309
        // 현재 인증된 사용자의 정보를 가져온다
        MemberDTO memberDTO = (MemberDTO)authentication.getPrincipal();
        Map<String, Object> claims = memberDTO.getClaims();

        claims.put("accessToken", ""); // 토큰값 저장할 곳 생성
        claims.put("refreshToken", "");

        Gson gson = new Gson();
        String jsonStr = gson.toJson(claims); // json 형태로 변환

        // 응답형식 context-type 설정
        response.setContentType("application/json");

        // 클라이언트에 JSON응답 전송
        PrintWriter printWriter = response.getWriter();
        printWriter.println(jsonStr);
        printWriter.close();
    }


}

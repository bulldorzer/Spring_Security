package org.zerock.mallapi.security.handler;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Log4j2
public class APILoginFailHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure
            (HttpServletRequest request,
             HttpServletResponse response,
             AuthenticationException exception)
            throws IOException, ServletException {
        log.info("Login Fail..." + exception);

        Gson gson = new Gson();
        String jsonStr = gson.toJson(Map.of("error", "ERROR_LOGIN")); // json 형태로 변환

        // 응답형식 context-type 설정
        response.setContentType("application/json");

        // 클라이언트에 JSON응답 전송
        PrintWriter printWriter = response.getWriter();
        printWriter.println(jsonStr);
        printWriter.close();
    }
}

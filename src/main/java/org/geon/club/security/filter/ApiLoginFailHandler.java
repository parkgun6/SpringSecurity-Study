package org.geon.club.security.filter;

import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Log4j2
public class ApiLoginFailHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("Login Fail Handler................");
        log.info(exception.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        response.setContentType("application/json;charset=utf-8");

        JSONObject json = new JSONObject();

        String message = exception.getMessage();

        json.put("code", "401");
//        json.put("message", message);
        json.put("message", "아이디 혹은 패스워드가 맞지 않습니다.");

        PrintWriter out = response.getWriter();
        // 화면으로 json Data 전송
        out.print(json);
    }
}

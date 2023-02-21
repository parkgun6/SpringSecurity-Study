package org.geon.club.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.geon.club.security.util.JWTUtil;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Log4j2
public class ApiCheckFilter extends OncePerRequestFilter {

    // SecuriotyConfig에 현재 class호출부에 패턴을 넣고 패턴에 맞는경우 다른동작을 하게 한다.
    // 현재는 /notes/**/* 에 맞는 패턴일 경우 동작한다
    private AntPathMatcher antPathMatcher;

    private String pattern;

    private JWTUtil jwtUtil;

    public ApiCheckFilter(String pattern, JWTUtil jwtUtil) {
        this.antPathMatcher = new AntPathMatcher();
        this.pattern = pattern;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("RequestUrl : " + request.getRequestURI());
        log.info(antPathMatcher.match(pattern, request.getRequestURI()));

        if (antPathMatcher.match(pattern, request.getRequestURI())) {
            log.info("-------------ApiCheckFilter---------------");
            log.info("-------------ApiCheckFilter---------------");
            log.info("-------------ApiCheckFilter---------------");

            boolean checkHeader = checkAuthHeader(request);

            if (checkHeader) {
                filterChain.doFilter(request, response);
                return;
            }else {
                // 인증이 실패해도 200 status가 나는 것을 방지
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                // json 리턴 및 한글깨짐 수정.
                response.setContentType("application/json;charset=utf-8");
                JSONObject json = new JSONObject();
                String message = "FAIL CHECK API TOKEN";
                json.put("code", "403");
                json.put("message", message);
                PrintWriter out = response.getWriter();
                // 화면으로 json Data 전송
                out.print(json);

            }

            return;
        }


        filterChain.doFilter(request, response); // 다음필터로 넘어가기 위해 필요
    }

    private boolean checkAuthHeader(HttpServletRequest request) {

        boolean checkResult = false;

        String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            log.info("Authorization exist : " + authHeader);
            try {
                String email = jwtUtil.validateAndExtract(authHeader.substring(7));
                log.info("validate result : " + email);
                checkResult = email.length() > 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
//            if (authHeader.equals("12345678")) {
//                checkResult = true;
//            }
        }
        return checkResult;
    }
}

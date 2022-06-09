package org.geon.club.security.filter;

import lombok.extern.log4j.Log4j2;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
public class ApiCheckFilter extends OncePerRequestFilter {

    // SecuriotyConfig에 현재 class호출부에 패턴을 넣고 패턴에 맞는경우 다른동작을 하게 한다.
    // 현재는 /notes/**/* 에 맞는 패턴일 경우 동작한다
    private AntPathMatcher antPathMatcher;

    private String pattern;

    public ApiCheckFilter(String pattern) {
        this.antPathMatcher = new AntPathMatcher();
        this.pattern = pattern;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("RequestUrl : " + request.getRequestURI());
        log.info(antPathMatcher.match(pattern, request.getRequestURI()));

        if (antPathMatcher.match(pattern, request.getRequestURI())) {
            log.info("-------------ApiCheckFilter---------------");
            log.info("-------------ApiCheckFilter---------------");
            log.info("-------------ApiCheckFilter---------------");

            return;
        }


        filterChain.doFilter(request, response); // 다음필터로 넘어가기 위해 필요
    }
}

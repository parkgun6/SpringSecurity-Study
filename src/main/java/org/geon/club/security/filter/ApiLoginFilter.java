package org.geon.club.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.geon.club.security.dto.ClubAuthMemberDTO;
import org.geon.club.security.util.JWTUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;

@Log4j2
public class ApiLoginFilter extends AbstractAuthenticationProcessingFilter {

    private JWTUtil jwtUtil;

    public ApiLoginFilter (String defaultFilterProcessesUrl, JWTUtil jwtUtil) {
        super(defaultFilterProcessesUrl);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        log.info("--------ApiLoginFilter--------");
        log.info("attemptAuthentication");

//        String email = request.getParameter("email");
//        String pw = "1111";
        String email = request.getParameter("email");
        String pw = request.getParameter("pw");
        log.info(email);
        log.info(pw);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, pw);

        if (email == null) {
            throw new BadCredentialsException("email cannot be null");
        }

        return getAuthenticationManager().authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
        throws IOException, ServletException {

        log.info("ApiLoginFilter.............");
        log.info("successfulAuthentication : " + authResult);

        log.info(authResult.getPrincipal());

        String email = ((ClubAuthMemberDTO) authResult.getPrincipal()).getUsername();

        String token = null;

        try {
          token = jwtUtil.generateToken(email);

            response.setContentType("text/plain");
            // 화면으로 token Data 전송
            response.getOutputStream().write(token.getBytes());

            log.info(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

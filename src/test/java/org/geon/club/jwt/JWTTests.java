package org.geon.club.jwt;

import lombok.extern.log4j.Log4j2;
import org.geon.club.security.util.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Log4j2
public class JWTTests {

    // 스프링을 이용한 테스트가 아니기 때문에 JWTUtil 객체를 직접 만들어서 Test
    private JWTUtil jwtUtil;

    @BeforeEach
    public void testBefore() {
        log.info("testBefore............");

        jwtUtil = new JWTUtil();
    }

    @Test
    public void testEncode() throws Exception {
        String email = "user95@geon.com";

        String str = jwtUtil.generateToken(email);

        log.info(str);
    }

    @Test
    public void testValidate() throws Exception {
        String email = "user95@geon.com";

        String str = jwtUtil.generateToken(email);

        Thread.sleep(5000);

        String resultEmail = jwtUtil.validateAndExtract(str);

        log.info("resultEmail : " + resultEmail);
    }


}

package org.geon.club.security.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJws;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;

@Log4j2
public class JWTUtil {

    private String secretKey = "geon12345678";

    private long expire = 60 * 24 * 30;

    // 토큰을 생성하는 역할
    public String generateToken(String email) throws Exception {
        // JWT를 알면 누구나 API를 사용하기 때문에 만료기간을 설정한다.
        // geon1234578을 key로 지정하여 Signature를 생성한다.
        // sub라는 이름을 가지는 Claim에서는 사용자의 이메일주소를 입력해주어 나중에 사용할수 있게 구성한다.
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(expire).toInstant()))
                // 임시적으로 유효기간을 1초로 지정하고 Test 진행
//                .setExpiration(Date.from(ZonedDateTime.now().plusSeconds(1).toInstant()))
                .claim("sub", email)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes("UTF-8"))
                .compact();
    }

    // 인코딩된 문자열에서 원하는 값을 추출하는 용도
    // 만료기간이 지났을 경우 sub이름으로 보관된 이메일과 Exception을 반환한다.
    public String validateAndExtract(String tokenStr)throws Exception {
        String contentValue = null;

        try {
            DefaultJws defaultJws = (DefaultJws) Jwts.parser().setSigningKey(secretKey.getBytes("UTF-8"))
                    .parseClaimsJws(tokenStr);

            log.info(defaultJws);
            log.info(defaultJws.getBody().getClass());

            DefaultClaims claims = (DefaultClaims) defaultJws.getBody();

            log.info("-----------------------");
            contentValue = claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            contentValue = null;
        }
        return contentValue;
    }
}

package org.geon.club.repository;

import lombok.extern.log4j.Log4j2;
import org.geon.club.entity.ClubMember;
import org.geon.club.entity.ClubMemberRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class ClubMemberTests {

    @Autowired
    ClubMemberRepository clubMemberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void insertDummies() {
        //1 - 80까지는 USER만 지정
        //81- 90까지는 USER,MANAGER
        //91- 100까지는 USER,MANAGER,ADMIN

        IntStream.rangeClosed(1,100).forEach(i -> {
            ClubMember clubMember = ClubMember.builder()
                    .email("user" + i + "@geon.com")
                    .name("사용자" + i)
                    .fromSocial(false)
                    .roleSet(new HashSet<ClubMemberRole>())
                    .password(passwordEncoder.encode("1111") )
                    .build();

            //default role
            clubMember.addMemberRole(ClubMemberRole.USER);

            if(i > 80){
                clubMember.addMemberRole(ClubMemberRole.MANAGER);
            }

            if(i > 90){
                clubMember.addMemberRole(ClubMemberRole.ADMIN);
            }

            clubMemberRepository.save(clubMember);
        });
    }

    @Test
    public void testRead() {

        Optional<ClubMember> result = clubMemberRepository.findByEmail("user95@geon.com", false);

        ClubMember clubMember = result.get();

        log.info(clubMember);

    }
}

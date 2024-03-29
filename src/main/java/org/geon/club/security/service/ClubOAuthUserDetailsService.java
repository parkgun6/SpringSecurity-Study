package org.geon.club.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.geon.club.entity.ClubMember;
import org.geon.club.entity.ClubMemberRole;
import org.geon.club.repository.ClubMemberRepository;
import org.geon.club.security.dto.ClubAuthMemberDTO;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ClubOAuthUserDetailsService extends DefaultOAuth2UserService {

    private final ClubMemberRepository clubMemberRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("--------------------------------------------------");
        log.info("userRequest : " + userRequest);

        String clientName = userRequest.getClientRegistration().getClientName();

        log.info("clientName : " + clientName);
        log.info(userRequest.getAdditionalParameters());

        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("---------------------------------------------------");

        oAuth2User.getAttributes().forEach((k,v) -> {
            log.info(k + " : " + v);
        });

        String email = null;

        if (clientName.equals("Google")) {
            email = oAuth2User.getAttribute("email");
        }

        log.info("EMAIL : " + email);

//        ClubMember member = saveSocialMember(email);
//
//        return oAuth2User;

        ClubMember member = saveSocialMember(email);

        // saveSocialMember 결과로 clubAuthMember를 구성한다.
        ClubAuthMemberDTO clubAuthMember = new ClubAuthMemberDTO(
                member.getEmail(),
                member.getPassword(),
                true,
                member.getRoleSet().stream().map(
                        role -> new SimpleGrantedAuthority("ROLE_" + role.name())
                ).collect(Collectors.toList()),
                oAuth2User.getAttributes()
        );

        clubAuthMember.setName(member.getName());

        return clubAuthMember;
    }

    private ClubMember saveSocialMember(String email) {
        Optional<ClubMember> result = clubMemberRepository.findByEmail(email, true);

        if (result.isPresent()) {
            return result.get();
        }

        ClubMember clubMember = ClubMember.builder()
                .email(email)
                .name(email)
                .password(passwordEncoder.encode("1111"))
                .fromSocial(true)
                .build();

        clubMember.addMemberRole(ClubMemberRole.USER);

        clubMemberRepository.save(clubMember);

        return clubMember;
    }
}

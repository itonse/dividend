package com.itonse.dividend.persist.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "MEMBER")
public class MemberEntity implements UserDetails {   // 스프링 시큐리티에서 지원해주는 기능 사용

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER) //
    private List<String> roles;    // 사용자가 read, write 권한 둘 다 가질 수 있기 때문에 리스트로 사용

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()  // role 관련 기능 사용하기 위해 맵핑
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
    }


    //// 아래 기능들 사용x

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}

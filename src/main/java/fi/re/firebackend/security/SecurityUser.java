package fi.re.firebackend.security;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class SecurityUser implements UserDetails {

    private String username;    // id
    private String password;
    private String name;        // nickname
    private String auth;  // ROLE_USER, ROLE_MANAGER, ROLE_ADMIN

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
        auth.add(new SimpleGrantedAuthority("authority"));  // ROLE_USER, ROLE_ADMIN
        return auth;
    }


    // 계정만료
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정잠김여부
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호 만료여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 사용자 활성화 여부
    @Override
    public boolean isEnabled() {
        return true;
    }
}

package com.lemon.blog.security.token;

import com.lemon.blog.dto.AuthUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
@Setter
public class JwtAuthToken extends AbstractAuthenticationToken {

    private Object principal;

    private Object credentials;

    public JwtAuthToken(String jwt) {
        super(null);
        this.credentials = jwt;
        setAuthenticated(false);
    }

    public JwtAuthToken(AuthUser authUser) {
        super(authUser.getAuthorities());
        this.principal = authUser;
        this.credentials = null;
        setAuthenticated(true);
    }
}

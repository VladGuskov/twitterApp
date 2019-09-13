package vladeater.domain;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author Vlados Guskov
 */
public enum Role implements GrantedAuthority {
    USER, ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}

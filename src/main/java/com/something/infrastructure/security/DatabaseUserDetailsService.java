package com.something.infrastructure.security;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final JdbcTemplate jdbcTemplate;

    public DatabaseUserDetailsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String sql = """
                select username, password_hash, role, status
                from users
                where username = ?
                """;

        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) {
                throw new UsernameNotFoundException("User not found: " + username);
            }

            String dbUsername = rs.getString("username");
            String passwordHash = rs.getString("password_hash");
            String role = rs.getString("role");
            String status = rs.getString("status");
            boolean enabled = "ACTIVE".equalsIgnoreCase(status);
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

            return User.withUsername(dbUsername)
                    .password(passwordHash)
                    .authorities(authorities)
                    .disabled(!enabled)
                    .build();
        }, username);
    }
}

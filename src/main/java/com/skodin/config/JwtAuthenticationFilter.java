package com.skodin.config;

import com.skodin.services.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain filterChain) throws IOException, ServletException {
        try {
            String jwt = ((HttpServletRequest) servletRequest).getHeader("Authorization");

            if (jwt != null && jwt.startsWith("Bearer ")) {
                jwt = jwt.substring(7);
                log.debug("Attempting to authorize with JWT Token: [{}...], length: {}", jwt.substring(0, 6), jwt.length());

                if (jwtService.isTokenValid(jwt)) {
                    String username = jwtService.extractUsername(jwt);
                    log.debug("JWT Token is valid. Extracted username: {}", username);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    log.debug("Loaded UserDetails for username: {}", username);

                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            jwtService.extractAllClaims(jwt),
                            userDetails.getAuthorities()
                    );

                    log.info("Successfully authorized user: {}", username);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.warn("Invalid JWT Token: [{}...], length: {}", jwt.substring(0, 6), jwt.length());
                }
            }
        } catch (Exception e) {
            log.error("Exception occurred during authorization: {}", e.getMessage(), e);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
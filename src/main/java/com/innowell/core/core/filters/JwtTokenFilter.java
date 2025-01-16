package com.innowell.core.core.filters;

import com.innowell.core.core.models.CustomUserDetails;
import com.innowell.core.features.auth.entity.LoginUrl;
import com.innowell.core.features.auth.entity.User;
import com.innowell.core.features.auth.repository.LoginUrlRepository;
import com.innowell.core.features.auth.service.AuthTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final AuthTokenService authTokenService;

    private final LoginUrlRepository loginUrlRepository;

    public JwtTokenFilter(AuthTokenService authTokenService, LoginUrlRepository loginUrlRepository) {
        this.authTokenService = authTokenService;
        this.loginUrlRepository = loginUrlRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        // get authorization header and validate
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String clientName = request.getHeader("clientName");
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // get jwt token and validate
        final String token = header.split(" ")[1].trim();

        // get user identity and set it on the spring security context
        UserDetails userDetails = null;
        try {
            Optional<LoginUrl> loginUrl = loginUrlRepository.findByClientName(clientName);
            User casdoorUser = authTokenService.parseJwtToken(token, loginUrl.get().getClientCertificate());
            userDetails = new CustomUserDetails(casdoorUser);
        } catch (RuntimeException exception) {
            logger.error("casdoor auth exception", exception);
            chain.doFilter(request, response);
            return;
        }

        request.setAttribute("user", userDetails);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

}
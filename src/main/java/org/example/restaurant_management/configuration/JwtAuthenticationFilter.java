package org.example.restaurant_management.configuration;

import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.restaurant_management.exception.AppException;
import org.example.restaurant_management.service.AuthenticationService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    final AuthenticationService authenticationService;

    // Các endpoint không cần token
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/auth/login",
            "/api/auth/introspect",
            "/user"
    );

    // Các endpoint chỉ cần Access Token (không cần restaurant context)
    private static final List<String> ACCESS_TOKEN_ONLY = List.of(
            "/api/restaurants/join",        // tham gia nhà hàng bằng invite code
            "/api/restaurants/my-list",     // xem danh sách nhà hàng của tôi
            "/api/restaurants/{id}/session" // lấy context token
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private boolean isAccessTokenOnlyEndpoint(String path) {
        return ACCESS_TOKEN_ONLY.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Bỏ qua public endpoints
        if (PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        try {
            SignedJWT jwt = SignedJWT.parse(token);
            String tokenType = jwt.getJWTClaimsSet().getStringClaim("tokenType");

            // sau khi parse được tokenType
            if ("context".equals(tokenType) && isAccessTokenOnlyEndpoint(path)) {
                sendUnauthorized(response, "This endpoint requires access token, not context token");
                return;
            }

            if ("access".equals(tokenType)) {
                // Verify access token
                authenticationService.verifyToken(token, "access");
                Long userId = jwt.getJWTClaimsSet().getLongClaim("userId");

                // Tạo principal chỉ có userId, chưa có role
                AccessTokenPrincipal principal = new AccessTokenPrincipal(userId,
                        jwt.getJWTClaimsSet().getSubject());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal, null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else if ("context".equals(tokenType)) {
                // Verify context token
                authenticationService.verifyToken(token, "context");
                Long userId = jwt.getJWTClaimsSet().getLongClaim("userId");
                Long restaurantId = jwt.getJWTClaimsSet().getLongClaim("restaurantId");
                String role = jwt.getJWTClaimsSet().getStringClaim("role");

                // Tạo principal đầy đủ với restaurantId + role
                ContextTokenPrincipal principal = new ContextTokenPrincipal(
                        userId, restaurantId, role);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal, null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role)));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else {
                sendUnauthorized(response, "Unknown token type");
                return;
            }

            filterChain.doFilter(request, response);

            System.out.println("Authenticated: " + SecurityContextHolder.getContext().getAuthentication());

        } catch (AppException e) {
            sendUnauthorized(response, e.getMessage());
        } catch (Exception e) {
            sendUnauthorized(response, "Invalid token");
        }
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
                String.format("{\"error\": \"UNAUTHORIZED\", \"message\": \"%s\"}", message));
    }
}

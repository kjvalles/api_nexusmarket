package application.adapters.security;

import application.domain.models.User;
import application.domain.ports.out.JwtServicePort;
import application.domain.valueobjects.UserStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación HTTP que intercepta tokens Bearer, valida su firma y
 * verifica la vigencia de la versión de token (authTokenVersion) para rechazar sesiones revocadas.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtServicePort jwtServicePort;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractBearerToken(request);

        if (token != null && jwtServicePort.validateToken(token)) {
            try {
                User user = jwtServicePort.reconstructUser(token);
                Integer tokenVersion = jwtServicePort.extractTokenVersion(token);

                if (isTokenValidForUser(user, tokenVersion)) {
                    AuthenticatedUserPrincipal principal = new AuthenticatedUserPrincipal(user);
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    SecurityContextHolder.clearContext();
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isTokenValidForUser(User user, Integer tokenVersion) {
        if (user == null || !user.isActive()) {
            return false;
        }
        Integer expectedVersion = user.getAuthTokenVersion() != null ? user.getAuthTokenVersion() : 1;
        return expectedVersion.equals(tokenVersion);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}

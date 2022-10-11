package Vibee.Jwt;

import Vibee.Jwt.util.JwtConstant;
import Vibee.Jwt.util.JwtUtil;
import Vibee.Service.TokenService;
import Vibee.Service.UserServiceImplUserDetailService;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private TokenService tokenService;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserServiceImplUserDetailService userServiceImplUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            String header = request.getHeader(JwtConstant.AUTHORIZATION_HEADER_STRING);
            if (header != null && header.startsWith(JwtConstant.TOKEN_BEARER_PREFIX)) {

                UsernamePasswordAuthenticationToken authentication = authorizeRequest(request);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken authorizeRequest(HttpServletRequest request) {
        try {
            // Get token.
            String token = this.jwtUtil.extractToken(request);
            if (token != null) {
                // Get token key.
                JwtModel model = (JwtModel) this.tokenService.getSecretKey(token);
                // Validate token.
                Claims claims = this.jwtHelper.validateToken(model.getSecretKey(), model);
                // Validate user authority/role if allowed to do the api dto.
                String user = claims.getSubject();
                UserDetails userDetails = this.userServiceImplUserDetailService.loadUserByUsername(user);
                if (userDetails != null) {
                    return new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }
        return null;
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }
}

package site.globitokuki.globitokuki_backend.configs;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import site.globitokuki.globitokuki_backend.dtos.ResponseDTO;
import site.globitokuki.globitokuki_backend.utils.JwtUtils;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final UserDetailsService userDetailsService;
  private final JwtUtils jwtUtils;

  public JwtAuthenticationFilter(UserDetailsService userDetailsService, JwtUtils jwtUtils) {
    this.userDetailsService = userDetailsService;
    this.jwtUtils = jwtUtils;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = jwtUtils.getTokenFromRequest(request);

    if (token == null && !isPublicUrl(request.getRequestURI())) {
      errorResponse(response, "Recurso protegido.", HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    if (isPublicUrl(request.getRequestURI())) {
      filterChain.doFilter(request, response);
      return;
    }

    if (token != null) {
      String username = jwtUtils.getUsernameFromToken(token);
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      if (jwtUtils.isTokenValid(token, userDetails)) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      } else {
        errorResponse(response, "Token inválido.", HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private void errorResponse(HttpServletResponse response, String error, Integer code) throws IOException {
    ResponseDTO errorResponse = new ResponseDTO(error, code);
    response.setStatus(code);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    new ObjectMapper().writeValue(response.getWriter(), errorResponse);
  }

  private boolean isPublicUrl(String url) {
    return url.startsWith("/globitokuki/auth");
  }
}

package com.example.resourcemanager.filter;

import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.entity.User;
import com.example.resourcemanager.entity.LoginUser;
import com.example.resourcemanager.enums.ExceptionEnum;
import com.example.resourcemanager.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;

@AllArgsConstructor
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final RedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = tokenService.getRequestToken(request);
            if ("/user/login".equals(request.getRequestURI()) || "/user/code".equals(request.getRequestURI())) {
                filterChain.doFilter(request, response);
                return;
            }

            if (token == null || token.trim().isEmpty()) throw new AuthenticationException("请先登录");

            String userName = tokenService.getUserNameFromToken(token);
            String key = tokenService.getTokenKey(userName);
            User user = (User) redisTemplate.opsForValue().get(key);
            if(user == null){
                 throw new AuthenticationException("用户登录已过期");
            }
            LoginUser loginUser = new LoginUser();
            loginUser.setUser(user);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser,null,null);
//            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            tokenService.verifyToken(user);
            filterChain.doFilter(request, response);
        } catch (AuthenticationException exception) {
            ObjectMapper mapper = new ObjectMapper();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println(mapper.writeValueAsString(ResultResponse.error(ExceptionEnum.NOT_AUTHORITY)));
        }
    }
}

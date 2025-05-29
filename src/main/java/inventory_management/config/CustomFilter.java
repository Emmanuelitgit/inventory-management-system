package inventory_management.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import inventory_management.exception.UnAuthorizeException;
import inventory_management.service.AuthenticationService;
import inventory_management.utils.AppUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomFilter extends OncePerRequestFilter {

    private final AuthenticationService authenticationService;
    private final AppUtils appUtils;

    @Autowired
    public CustomFilter(AuthenticationService authenticationService, AppUtils appUtils) {
        this.authenticationService = authenticationService;
        this.appUtils = appUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       try {
           String auth = request.getHeader("Authorization");
           if (auth!=null){
               String token = auth.substring(7);
               authenticationService.isTokenValid(token);
               String username = authenticationService.extractUsername(token);
               Object userId = authenticationService.extractUserId(token);

               UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null, null);
               SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
           }
           filterChain.doFilter(request, response);

       }catch (UnAuthorizeException ex) {
           response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
           response.setContentType("application/json");
           Map<String, Object> res = new HashMap<>();
           res.put("message", ex.getMessage());
           res.put("statusCode", HttpStatus.valueOf(401));
           res.put("date", new Date());
           ObjectMapper mapper = new ObjectMapper();
           String responseData = mapper.writeValueAsString(res);
           response.getWriter().write(responseData);
       }
    }
}

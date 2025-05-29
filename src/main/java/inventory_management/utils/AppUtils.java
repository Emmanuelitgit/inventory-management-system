package inventory_management.utils;

import inventory_management.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;


@Slf4j
@Component
public class AppUtils {

    /**
     * This method is used to handle all responses in the application.
     * @param message
     * @param status
     * @return responseDto object
     * @auther
     * @createdAt 29th, May 2025
     */
    public static ResponseDTO getResponseDto(String message, HttpStatus status){
        ResponseDTO responseDto = new ResponseDTO();
        responseDto.setMessage(message);
        responseDto.setDate(ZonedDateTime.now());
        responseDto.setStatusCode(status.value());
        return responseDto;
    }

    /**
     * This method is used to handle all responses in the application.
     * @param message
     * @param status
     * @param data
     * @return responseDto object
     * @auther
     * @createdAt 29th, May 2025
     */
    public static ResponseDTO getResponseDto(String message, HttpStatus status, Object data){
        if(data==null){
            ResponseDTO responseDto = getResponseDto(message, status);
            return responseDto;
        }
        ResponseDTO responseDto = new ResponseDTO();
        responseDto.setMessage(message);
        responseDto.setDate(ZonedDateTime.now());
        responseDto.setStatusCode(status.value());
        responseDto.setData(data);
        return responseDto;
    }

    /**
     * This method is used to get user full name.
     * @param first
     * @param last
     * @return responseDto object
     * @auther Emmanuel Yidana
     * @createdAt 29th, May 2025
     */
    public static String getFullName(String first, String last){
        return first + " " + " " + last;
    }

    /**
     * This method is used to set authenticated user authorities.
     * @param username
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
//    public void setAuthorities(String username, Object userId) {
//        String role = getUserRole(username);
//        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
//        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
//        grantedAuthorities.add(authority);
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//                userId, null, grantedAuthorities
//        );
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//    }

}

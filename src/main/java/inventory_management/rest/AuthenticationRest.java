package inventory_management.rest;

import inventory_management.dto.ResponseDTO;
import inventory_management.models.User;
import inventory_management.repo.UserRepo;
import inventory_management.service.AuthenticationService;
import inventory_management.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/users/authenticate")
public class AuthenticationRest {

    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepo;
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationRest(AuthenticationManager authenticationManager, UserRepo userRepo, AuthenticationService authenticationService) {
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
        this.authenticationService = authenticationService;
    }

    /**
     * @description This method is used to authenticate users abd generate token on authentication success.
     * @param credentials
     * @return
     * @auther
     * @createdAt 29th, May 2025
     */
    @PostMapping
    public ResponseEntity<ResponseDTO> authenticateUser(@RequestBody User credentials){
        log.info("In authentication method:=========");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.getEmail(),
                        credentials.getPassword()
                )
        );

        if (!authentication.isAuthenticated()){
            log.info("Authentication fail:=========");
            ResponseDTO  response = AppUtils.getResponseDto("invalid credentials", HttpStatus.UNAUTHORIZED);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String token = authenticationService.generateToken(credentials.getEmail(), credentials.getId());
        Map<String, String> tokenData = new HashMap<>();
        tokenData.put("email", credentials.getEmail());
        tokenData.put("full name",AppUtils.getFullName(credentials.getFirstName(), credentials.getLastName()));
        tokenData.put("token", token);
        log.info("Authentication success:=========");
        ResponseDTO  response = AppUtils.getResponseDto("authentication successfully", HttpStatus.OK, tokenData);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

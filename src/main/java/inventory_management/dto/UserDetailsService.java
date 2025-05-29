package inventory_management.dto;

import inventory_management.exception.UnAuthorizeException;
import inventory_management.models.User;
import inventory_management.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepo userRepo;

    @Autowired
    public UserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepo.findUserByEmail(username);
        if (userOptional.isEmpty()){
           throw new UnAuthorizeException("Invalid credentials");
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password(userOptional.get().getPassword())
                .build();
    }
}

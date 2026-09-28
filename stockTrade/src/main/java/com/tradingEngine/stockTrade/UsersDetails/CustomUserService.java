package com.tradingEngine.stockTrade.UsersDetails;

import com.tradingEngine.stockTrade.JPARepository.UserRepositoryJPA;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserService implements UserDetailsService {

    private final UserRepositoryJPA userRepositoryJPA;

    public CustomUserService(UserRepositoryJPA userRepositoryJPA){
        this.userRepositoryJPA = userRepositoryJPA;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Direct tumhari model User return karo (kyunki User implements UserDetails hai)
        return userRepositoryJPA.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}

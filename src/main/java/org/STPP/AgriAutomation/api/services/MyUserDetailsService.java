package org.STPP.AgriAutomation.api.services;

import org.STPP.AgriAutomation.api.repositories.UserRepo;
import org.STPP.AgriAutomation.data.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            // It's better to use a logger instead of System.out.println
            // logger.warn("User not found: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        
        return user; // Since User implements UserDetails
    }
}

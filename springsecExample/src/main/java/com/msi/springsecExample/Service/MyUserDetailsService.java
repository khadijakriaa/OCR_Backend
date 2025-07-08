package com.msi.springsecExample.Service;

import com.msi.springsecExample.model.UserPrincipal;
import com.msi.springsecExample.model.Users;
import com.msi.springsecExample.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepo repo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       Users user1 =repo.findByUsername(username);
       if(user1== null){
           System.out.println("User not found");
           throw new UsernameNotFoundException("User not found");

       }
        return new UserPrincipal(user1);
    }
}

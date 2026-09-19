package com.openecom.ecom.service;


import com.openecom.ecom.dto.UserRequestDTO;
import com.openecom.ecom.entity.User;
import com.openecom.ecom.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserService, UserDetailsPasswordService {

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userDetailsRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }


    @Override
    public void addUser(UserRequestDTO userRequestDTO) {
        User user = new User();

        user.setUsername(userRequestDTO.getUsername());
        user.setFirstName(userRequestDTO.getFirstName());
        user.setUserLastName(userRequestDTO.getUserLastName());
        user.setUserAddress(userRequestDTO.getUserAddress());
        user.setEmail(userRequestDTO.getEmail());

        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));

        userDetailsRepository.save(user);
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        User updatedUser = userDetailsRepository.findUserByUsername(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        updatedUser.setPassword(newPassword);

        return userDetailsRepository.save(updatedUser);
    }
}

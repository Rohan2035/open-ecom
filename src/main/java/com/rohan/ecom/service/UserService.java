package com.openecom.ecom.service;

import com.openecom.ecom.dto.UserRequestDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    void addUser(UserRequestDTO userRequestDTO);
}

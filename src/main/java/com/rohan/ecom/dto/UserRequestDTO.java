package com.openecom.ecom.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {

    private String username;
    private String firstName;
    private String userLastName;
    private String password;
    private String email;
    private String userAddress;
}

package com.jc.catalogo.dto;

import com.jc.catalogo.entities.User;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    Set<RoleDTO> roles = new HashSet<>();

    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
    }

}

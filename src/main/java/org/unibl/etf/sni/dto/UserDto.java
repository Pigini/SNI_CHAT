package org.unibl.etf.sni.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private int id;
    private String username;
    private String password;
    private Integer salt;
    private Integer hashCount;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private boolean active;

}

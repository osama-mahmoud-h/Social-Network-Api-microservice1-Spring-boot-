package com.example.server.payload.request.profile;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ContactInfoDto {

    @Max(value = 16,message = "too long phone number")
    private String phone;

    @Max(value = 16,message = "too long telephone number")
    private String telephone;

    @Email(message = "not valid email address")
    @Max(value = 32,message = "too long email address")
    private String email;

    @Max(value = 128,message = "too long address name")
    private String Address;

}

package com.example.server.payload.request.profile;

import lombok.*;

import javax.validation.constraints.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ContactInfoDto {

    @Size(max = 16,message = "too long phone number")
    //@NotBlank(message = "please provide username")
    private String phone;

    @Size(max = 16,message = "too long telephone number")
    private String telephone;

    @Email(message = "not valid email address")
    @Size(max = 32,message = "too long email address")
    private String email;

    @Size(max = 128,message = "too long address name")
    private String address;

}

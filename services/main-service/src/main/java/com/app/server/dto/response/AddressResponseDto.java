package com.app.server.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponseDto {
    private Long addressId;

    private String street;

    private String city;

    private String state;

    private String country;

    private String zipCode;

}

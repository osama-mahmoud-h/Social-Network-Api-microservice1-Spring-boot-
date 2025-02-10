package com.app.server.mapper;

import com.app.server.dto.response.AddressResponseDto;
import com.app.server.model.Address;
import org.springframework.stereotype.Service;

@Service
public class AddressMapper {
    public AddressResponseDto mapToAddressResponseDto(Address address) {
        return AddressResponseDto.builder()
                .addressId(address.getAddressId())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .zipCode(address.getZipCode())
                .build();
    }
}

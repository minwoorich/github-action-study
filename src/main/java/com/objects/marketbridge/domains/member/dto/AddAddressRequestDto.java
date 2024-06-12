package com.objects.marketbridge.domains.member.dto;

import com.objects.marketbridge.domains.member.domain.Address;
import com.objects.marketbridge.domains.member.domain.AddressValue;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddAddressRequestDto {

    @NotNull
    private String phoneNo;
    @NotNull
    private String name;
    @NotNull
    private String city;
    @NotNull
    private String street;
    @NotNull
    private String zipcode;
    @NotNull
    private String detail;
    @NotNull
    private String alias;
    @NotNull
    private Boolean isDefault;

    @Builder
    public AddAddressRequestDto(String phoneNo, String name, String city, String street, String zipcode, String detail, String alias, Boolean isDefault) {
        this.phoneNo = phoneNo;
        this.name = name;
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
        this.detail = detail;
        this.alias = alias;
        this.isDefault=isDefault;
    }

    public Address toEntity() {
        return Address.builder()
                .addressValue(AddressValue.builder().phoneNo(phoneNo).name(name).city(city).street(street).zipcode(zipcode).detail(detail).alias(alias).build())
                .isDefault(isDefault)
                .build();
    }
}

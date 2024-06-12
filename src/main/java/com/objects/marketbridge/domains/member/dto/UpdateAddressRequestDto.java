package com.objects.marketbridge.domains.member.dto;

import com.objects.marketbridge.domains.member.domain.Address;
import com.objects.marketbridge.domains.member.domain.AddressValue;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateAddressRequestDto {

    private String phoneNo;
    private String name;
    private String city;
    private String street;
    private String zipcode;
    private String detail;
    private String alias;
    private Boolean isDefault;

    @Builder
    public UpdateAddressRequestDto(String phoneNo, String name, String city, String street, String zipcode, String detail, String alias,Boolean isDefault) {
        this.phoneNo = phoneNo;
        this.name = name;
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
        this.detail = detail;
        this.alias = alias;
        this.isDefault=isDefault;
    }
}

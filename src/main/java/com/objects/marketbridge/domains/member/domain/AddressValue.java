package com.objects.marketbridge.domains.member.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AddressValue {

    private String phoneNo;
    private String name;
    private String city;
    private String street;
    private String zipcode;
    private String detail;

    private String alias;

    @Builder
    private AddressValue(String phoneNo, String name, String city, String street, String zipcode, String detail, String alias) {
        this.phoneNo = phoneNo;
        this.name = name;
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
        this.detail = detail;
        this.alias = alias;
    }

    public static AddressValue create(String phoneNo, String name, String city, String street, String zipcode, String detail, String alias) {
        return AddressValue.builder()
                .phoneNo(phoneNo)
                .name(name)
                .city(city)
                .street(street)
                .zipcode(zipcode)
                .detail(detail)
                .alias(alias)
                .build();
    }
}

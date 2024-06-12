package com.objects.marketbridge.domains.member.dto;

import com.objects.marketbridge.domains.member.domain.Address;
import com.objects.marketbridge.domains.member.domain.AddressValue;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetAddressesResponse {

    private Long addressId;

    private AddressValue addressValue;

    private Boolean isDefault;

    @Builder
    public GetAddressesResponse(Long addressId ,AddressValue addressValue, Boolean isDefault) {
        this.addressId =addressId;
        this.addressValue = addressValue;
        this.isDefault = isDefault;
    }

    public static GetAddressesResponse of(Address address){
        return GetAddressesResponse.builder()
                .addressId(address.getId())
                .addressValue(address.getAddressValue())
                .isDefault(address.getIsDefault()).build();
    }
}

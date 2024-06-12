package com.objects.marketbridge.domains.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Embedded
    private AddressValue addressValue;

    private Boolean isDefault;


    @Builder
    private Address(Member member, AddressValue addressValue, boolean isDefault) {
        this.member = member;
        this.addressValue = addressValue;
        this.isDefault = isDefault;
    }

    // 연관관계 편의 메서드 -> Address 쪽에서 한번에 저장
    public void setMember(Member member) {
        if (this.member != null) {
            this.member.getAddresses().remove(this);
        }
        this.member = member;
    }

    public void update(AddressValue addressValue , Boolean isDefault){
        this.addressValue=addressValue;
        this.isDefault = isDefault;
    }

    public void update(Boolean isDefault){ this.isDefault= isDefault;}

    // 비즈니스 로직
    public static Address create(AddressValue addressValue, Boolean isDefault) {
        return Address.builder()
                .addressValue(addressValue)
                .isDefault(isDefault)
                .build();
    }
}

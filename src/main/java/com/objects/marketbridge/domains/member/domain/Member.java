package com.objects.marketbridge.domains.member.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Setter
    private String membership;

    private String email;

    private String password;

    private String name;

    private String phoneNo;

    // 알림
    private Boolean isAlert;

    // 약관동의
    private Boolean isAgree;


    // 양방향 설정
    @OneToMany(mappedBy = "member" , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    @Builder
    public Member(Long id, String membership, String email, String password, String name, String phoneNo, Boolean isAlert, Boolean isAgree) {
        this.id = id;
        this.membership = membership;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNo = phoneNo;
        this.isAlert = isAlert;
        this.isAgree = isAgree;
    }

    // Member <-> Address 연관관계 편의 메서드
    public void addAddress(Address address) {
        if (!this.addresses.contains(address)) {
            this.addresses.add(address);
        }
        address.setMember(this);
    }

    // 비즈니스 로직
    public static Member create(String membership, String email, String password, String name, String phoneNo, Boolean isAlert, Boolean isAgree) {
        return Member.builder()
                .membership(membership)
                .email(email)
                .password(password)
                .name(name)
                .phoneNo(phoneNo)
                .isAlert(isAlert)
                .isAgree(isAgree)
                .build();
    }

    public void updateMemberInfo(String email, String name, String password, String phoneNo, Boolean isAlert, Boolean isAgree) {
        this.email=email;
        this.name=name;
        this.password=password;
        this.phoneNo=phoneNo;
        this.isAlert=isAlert;
        this.isAgree=isAgree;
    }

    public void updatePassword(String password) {
        this.password=password;
    }

}



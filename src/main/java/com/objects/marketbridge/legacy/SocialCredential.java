//package com.objects.marketbridge.member.domain;
//
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//// 소셜정보
//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class SocialCredential extends BaseEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "social_credential_id")
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
//    private Member memberId;
//
//    private String tokenId;
//
//    @Builder
//    private SocialCredential(Member memberId, String tokenId) {
//        this.memberId = memberId;
//        this.tokenId = tokenId;
//    }
//}

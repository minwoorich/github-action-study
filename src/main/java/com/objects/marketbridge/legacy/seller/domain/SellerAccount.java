//package com.objects.marketbridge.seller.domain;
//
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class SellerAccount {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "seller_account_id")
//    Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="seller_id")
//    Seller seller;
//
//    Long balance;
//    Long incoming;
//    Long outgoing;
//    String detail;
//
//    @Builder
//    public SellerAccount(Long balance, Long incoming, Long outgoing, Seller seller, String detail) {
//        this.seller = seller;
//        this.balance = balance;
//        this.incoming = incoming;
//        this.outgoing = outgoing;
//        this.detail = detail;
//    }
//
//    // 연관관계 편의 메서드
//    public void linkSeller(Seller seller) {
//        this.seller = seller;
//    }
//
//    // 비즈니스 로직
//    public static SellerAccount create(Long incoming, Long outgoing, Long balance, String detail) {
//
//        return SellerAccount.builder()
//                .balance(balance)
//                .incoming(incoming)
//                .outgoing(outgoing)
//                .detail(detail)
//                .build();
//    }
//}

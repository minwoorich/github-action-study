//package com.objects.marketbridge.seller.domain;
//
//import com.objects.marketbridge.member.domain.BaseEntity;
//import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.BALANCE_INSUFFICIENT;
//
//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class Seller extends BaseEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "seller_id")
//    Long id;
//    String name;
//    String bizNo;
//    String owner;
//    String category;
//    String detail;
//    String address;
//    String licenseNo;
//    String email;
//    String accountNo;
//    Long balance;
//
//    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
//    List<SellerAccount> sellerAccounts = new ArrayList<>();
//
//    @Builder
//    private Seller(String name, String bizNo, String owner, String category, String detail, String address, String licenseNo, String email, String accountNo, Long balance) {
//        this.name = name;
//        this.bizNo = bizNo;
//        this.owner = owner;
//        this.category = category;
//        this.detail = detail;
//        this.address = address;
//        this.licenseNo = licenseNo;
//        this.email = email;
//        this.accountNo = accountNo;
//        this.balance = balance;
//    }
//
//    // 연관관계 메서드
//    public void linkSellerAccounts(SellerAccount sellerAccount) {
//        if (this.sellerAccounts.contains(sellerAccount)) {
//            sellerAccounts.remove(sellerAccount);
//        }
//        this.sellerAccounts.add(sellerAccount);
//        sellerAccount.linkSeller(this);
//    }
//
//    // 비즈니스 로직
//
//    public void updateBalance(Long amount) {
//        balanceValidation(amount);
//        balance+=amount;
//    }
//
//    private void balanceValidation(Long amount) {
//        if (balance + amount < 0) {
//            throw new CustomLogicException(BALANCE_INSUFFICIENT.toString());
//        }
//    }
//}

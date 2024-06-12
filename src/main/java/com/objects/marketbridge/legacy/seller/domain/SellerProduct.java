//package com.objects.marketbridge.seller.domain;
//
//import com.objects.marketbridge.member.domain.BaseEntity;
//import com.objects.marketbridge.product.domain.Product;
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class SellerProduct extends BaseEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "seller_product_id")
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "seller_id")
//    private Seller sellerId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id")
//    private Product productId;
//
//    @Builder
//    private SellerProduct(Seller sellerId, Product productId) {
//        this.sellerId = sellerId;
//        this.productId = productId;
//    }
//}

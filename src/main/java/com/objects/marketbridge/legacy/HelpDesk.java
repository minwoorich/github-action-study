//package com.objects.marketbridge.member.domain;
//
//import com.objects.marketbridge.order.domain.Order;
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
//public class HelpDesk extends BaseEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "help_desk_id")
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "order_id")
//    private Order orderId;
//    //private Long orderId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
//    private Member memberId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id")
//    private Product productId;
//
//    @Enumerated(EnumType.STRING)
//    private ContentType contentType;
//
//    private String content;
//
//    @Builder
//    private HelpDesk(Order orderId, Member memberId, Product productId, ContentType contentType, String content) {
//        this.orderId = orderId;
//        this.memberId = memberId;
//        this.productId = productId;
//        this.contentType = contentType;
//        this.content = content;
//    }
//}

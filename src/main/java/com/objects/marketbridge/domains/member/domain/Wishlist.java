package com.objects.marketbridge.domains.member.domain;

import com.objects.marketbridge.domains.product.domain.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wishlist extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    public Wishlist(Member member, Product product) {
        this.member = member;
        this.product = product;
    }

    public static Wishlist of(Product product,Member member){
        return Wishlist.builder()
                .member(member)
                .product(product).build();
    }

    public static Wishlist create(Product product,Member member){
        return Wishlist.builder()
                .member(member)
                .product(product).build();
    }

}

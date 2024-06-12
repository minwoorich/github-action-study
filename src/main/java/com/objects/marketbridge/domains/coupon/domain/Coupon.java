package com.objects.marketbridge.domains.coupon.domain;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.exception.exceptions.ErrorCode;
import com.objects.marketbridge.domains.member.domain.BaseEntity;
import com.objects.marketbridge.domains.product.domain.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long id;

    private String name;

    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private Long productGroupId;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberCoupon> memberCoupons = new ArrayList<>();

    private Long count;

    private Long minimumPrice;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Builder
    public Coupon(Long id, Product product, Long productGroupId, String name, Long price, Long count, Long minimumPrice, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.product = product;
        this.name = name;
        this.price = price;
        this.count = count;
        this.minimumPrice = minimumPrice;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Coupon create(String name, Long productGroupId, Long price, Long count, Long minimumPrice, LocalDateTime startDate, LocalDateTime endDate) {
        return Coupon.builder()
                .name(name)
                .productGroupId(productGroupId)
                .price(price)
                .count(count)
                .minimumPrice(minimumPrice)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    public void addMemberCoupon(MemberCoupon memberCoupon) {
        memberCoupons.remove(memberCoupon);
        memberCoupons.add(memberCoupon);
        memberCoupon.linkCoupon(this);
    }

//    public void addProduct(Product product) {
//        this.product = product;
//        this.productGroupId = product.parseProductGroupId();
//    }

    public Boolean filteredBy(Long memberId) {
        return memberCoupons.stream().anyMatch(mc -> mc.filterByMemberId(memberId) && !mc.getIsUsed());
    }

    public void decreaseCount() {

        if (count == null || count <= 0 ) {

            throw CustomLogicException.createBadRequestError(ErrorCode.COUPON_OUT_OF_STOCK);
        }
        count-=1;
    }
}

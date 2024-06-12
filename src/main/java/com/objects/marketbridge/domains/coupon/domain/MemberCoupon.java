package com.objects.marketbridge.domains.coupon.domain;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.exception.exceptions.ErrorCode;
import com.objects.marketbridge.common.utils.DateTimeHolder;
import com.objects.marketbridge.domains.member.domain.BaseEntity;
import com.objects.marketbridge.domains.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCoupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_coupon_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    private Boolean isUsed;

    private LocalDateTime usedDate;

    private LocalDateTime endDate;

    @Builder
    private MemberCoupon(Member member, Coupon coupon, Boolean isUsed, LocalDateTime usedDate, LocalDateTime endDate) {
        this.member = member;
        this.coupon = coupon;
        this.isUsed = isUsed;
        this.usedDate = usedDate;
        this.endDate = endDate;
    }

    public static MemberCoupon create(Member member, Coupon coupon) {
        return MemberCoupon.builder()
                .member(member)
                .coupon(coupon)
                .isUsed(false)
                .endDate(coupon.getEndDate())
                .build();
    }

    public void changeUsageInfo(DateTimeHolder dateTimeHolder) {
        if(Objects.isNull(dateTimeHolder))
            changeUsageInfo((LocalDateTime) null);
        else
            changeUsageInfo(dateTimeHolder.getTimeNow());
    }

    public void changeUsageInfo(LocalDateTime dateTime) {
        isUsed = !isUsed;
        usedDate = dateTime;
    }

    public void linkCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public void applyCouponUsage(Boolean isUsed, DateTimeHolder dateTimeHolder) {
        this.isUsed = isUsed;
        this.usedDate = dateTimeHolder.getTimeNow();
    }

    public Boolean filterByMemberId(Long memberId) {
        return member.getId().equals(memberId);
    }

    public Long getMinimumPrice() {
        return coupon.getMinimumPrice();
    }
}

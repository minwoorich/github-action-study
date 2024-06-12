package com.objects.marketbridge.domains.order.domain;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.utils.DateTimeHolder;
import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.member.domain.BaseEntity;
import com.objects.marketbridge.domains.product.domain.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE order_detail SET deleted_at = now() WHERE order_detail_id = ?")
@SQLRestriction("deleted_at is NULL")
@Slf4j
public class OrderDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_coupon_id")
    private MemberCoupon memberCoupon;

    private Long quantity;

    private String orderNo;

    private Long price;

    private String statusCode;

    private LocalDateTime deliveredDate;

    private String tid;

    private Long sellerId;

    private LocalDateTime cancelledAt;

    private Long reducedQuantity;

    @Builder
    private OrderDetail(Order order, String orderNo, String tid, Product product, MemberCoupon memberCoupon,  Long quantity, Long price, String statusCode, LocalDateTime deliveredDate, Long sellerId, LocalDateTime cancelledAt, Long reducedQuantity) {
        this.orderNo = orderNo;
        this.tid = tid;
        this.order = order;
        this.product = product;
        this.memberCoupon = memberCoupon;
        this.quantity = quantity;
        this.price = price;
        this.statusCode = statusCode;
        this.deliveredDate = deliveredDate;
        this.sellerId = sellerId;
        this.cancelledAt = cancelledAt;
        this.reducedQuantity = reducedQuantity;
    }

    // 연관관계 메서드
    public void setOrder(Order order) {
        this.order = order;
    }

    // 비즈니스 로직
    public void changeStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public static OrderDetail create(String tid, Order order, Product product, String orderNo, MemberCoupon memberCoupon, Long price, Long quantity, String statusCode, DateTimeHolder dateTimeHolder) {
        if (memberCoupon != null) {
            validMemberCoupon(memberCoupon, price, quantity, dateTimeHolder);
        }
        return OrderDetail.builder()
                .orderNo(orderNo)
                .tid(tid)
                .order(order)
                .product(product)
                .memberCoupon(memberCoupon)
                .price(price)
                .quantity(quantity)
                .statusCode(statusCode)
                .reducedQuantity(0L)
                .build();
    }

    private static void validMemberCoupon(MemberCoupon memberCoupon, Long price, Long quantity, DateTimeHolder dateTimeHolder) {

        if (isCouponAlreadyUsed(memberCoupon)){
            throw CustomLogicException.createBadRequestError(COUPON_ALREADY_USED);
        }

        if (isOrderBelowMinimumPrice(memberCoupon, price, quantity)){
            throw CustomLogicException.createBadRequestError(COUPON_CONDITION_VIOLATION);
        }

        if (isCouponExpired(memberCoupon, dateTimeHolder)){
            throw CustomLogicException.createBadRequestError(COUPON_EXPIRED);
        }
    }

    private static boolean isOrderBelowMinimumPrice(MemberCoupon memberCoupon, Long price, Long quantity) {
        // 멤버 쿠폰에서 최소 주문 가격을 가져옴
        Long minimumPrice = memberCoupon.getMinimumPrice();

        // 총 주문 가격 계산
        Long totalPrice = calculateTotalPrice(price, quantity);

        // 총 주문 가격이 최소 주문 가격보다 크거나 같은지 확인하여 리턴
        return totalPrice <= minimumPrice;
    }

    // 총 주문 가격 계산
    private static Long calculateTotalPrice(Long price, Long quantity) {
        return price * quantity;
    }

    private static boolean isCouponExpired(MemberCoupon memberCoupon, DateTimeHolder dateTimeHolder) {
        return memberCoupon.getEndDate().isBefore(dateTimeHolder.getTimeNow());
    }

    private static Boolean isCouponAlreadyUsed(MemberCoupon memberCoupon) {
        return memberCoupon.getIsUsed();
    }

    public static OrderDetail create(OrderDetail orderDetail, String statusCode) {
        return OrderDetail.builder()
                .orderNo(orderDetail.getOrderNo())
                .tid(orderDetail.getTid())
                .order(orderDetail.getOrder())
                .product(orderDetail.getProduct())
                .memberCoupon(orderDetail.getMemberCoupon())
                .quantity(orderDetail.getReducedQuantity())
                .price(orderDetail.getPrice())
                .statusCode(statusCode)
                .deliveredDate(orderDetail.getDeliveredDate())
                .sellerId(orderDetail.getSellerId())
                .cancelledAt(orderDetail.getCancelledAt())
                .build();
    }

    public CancelReturnStatusCode cancel(Long numberOfCancellations, DateTimeHolder dateTimeHolder) {
        // TODO 정책 정해야 함
        if (impossibleCancel())
            throw CustomLogicException.createBadRequestError(NON_CANCELLABLE_PRODUCT, dateTimeHolder);

        return changeStatus(numberOfCancellations, dateTimeHolder, StatusCodeType.ORDER_CANCEL.getCode());
    }

    public CancelReturnStatusCode returns(Long numberOfReturns, DateTimeHolder dateTimeHolder) {
        if (impossibleReturn())
            throw CustomLogicException.createBadRequestError(NON_RETURNABLE_PRODUCT, dateTimeHolder);

        return changeStatus(numberOfReturns, dateTimeHolder, StatusCodeType.RETURN_INIT.getCode());
    }

    public Integer totalAmount(Long quantity, LocalDateTime dateTime) {
        if(exceededQuantity(quantity))
            throw CustomLogicException.createBadRequestError(QUANTITY_EXCEEDED, dateTime);

        return (int) (price * quantity);
    }

    public Integer totalAmount() {
        return totalAmount(this.quantity, LocalDateTime.now());
    }

    public Integer totalAmount(Long quantity) {
       return totalAmount(quantity, LocalDateTime.now());
    }

    public Integer cancelAmount() {
        return (int) (reducedQuantity * price);
    }

    public void changeMemberCouponInfo(DateTimeHolder dateTimeHolder) {
        if (hasMemberCoupon())
            memberCoupon.changeUsageInfo(dateTimeHolder);
    }

    public void returnMemberCoupon() {
        changeMemberCouponInfo(null);
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    private CancelReturnStatusCode changeStatus(Long numberOfReduce, DateTimeHolder dateTimeHolder, String statusCode) {
        validateQuantity(numberOfReduce, dateTimeHolder);

        String previousStatusCode = updateProductAndStatus(numberOfReduce, statusCode);

        String cancelReturnStatusCode = determineReturnOrCancelStatus(statusCode);

        return new CancelReturnStatusCode(previousStatusCode, cancelReturnStatusCode);
    }

    private void validateQuantity(Long numberOfReduce, DateTimeHolder dateTimeHolder) {
        if (exceededQuantity(numberOfReduce)) {
            throw CustomLogicException.createBadRequestError(QUANTITY_EXCEEDED, dateTimeHolder);
        }
    }

    private String updateProductAndStatus(Long numberOfReduce, String statusCode) {
        String status = this.statusCode;

        this.product.increase(numberOfReduce);
        this.statusCode = statusCode;
        this.reducedQuantity += numberOfReduce;
        returnMemberCoupon();

        return status;
    }

    private String determineReturnOrCancelStatus(String statusCode) {
        boolean isCancel = statusCode.equals(StatusCodeType.ORDER_CANCEL.getCode());
        boolean isWhole = Objects.equals(quantity, reducedQuantity);

        if (isWhole) {
            return isCancel ? StatusCodeType.ORDER_CANCEL.getCode() : StatusCodeType.ORDER_RETURN.getCode();
        } else {
            return isCancel ? StatusCodeType.ORDER_PARTIAL_CANCEL.getCode() : StatusCodeType.ORDER_PARTIAL_RETURN.getCode();
        }
    }

    private boolean exceededQuantity(Long numberOfCancellations) {
        return this.reducedQuantity + numberOfCancellations > this.quantity;
    }

    private boolean impossibleCancel() {
        return Objects.equals(this.statusCode, StatusCodeType.DELIVERY_COMPLETED.getCode())
                || Objects.equals(this.statusCode, StatusCodeType.ORDER_PARTIAL_CANCEL.getCode());
    }

    private boolean hasMemberCoupon() {
        return memberCoupon != null;
    }

    private boolean impossibleReturn() {
        return !Objects.equals(StatusCodeType.DELIVERY_COMPLETED.getCode(), this.statusCode);
    }

}

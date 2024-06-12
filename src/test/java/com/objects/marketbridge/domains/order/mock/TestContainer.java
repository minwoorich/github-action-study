package com.objects.marketbridge.domains.order.mock;

import com.objects.marketbridge.common.utils.DateTimeHolder;
import com.objects.marketbridge.domains.cart.mock.FakeCartCommandRepository;
import com.objects.marketbridge.domains.cart.mock.FakeCartQueryRepository;
import com.objects.marketbridge.domains.cart.service.port.CartCommandRepository;
import com.objects.marketbridge.domains.cart.service.port.CartQueryRepository;
import com.objects.marketbridge.domains.coupon.mock.FakeCouponRepository;
import com.objects.marketbridge.domains.coupon.mock.FakeMemberCouponRepository;
import com.objects.marketbridge.domains.member.mock.FakeMemberRepository;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.order.controller.OrderCancelController;
import com.objects.marketbridge.domains.order.controller.OrderCancelReturnController;
import com.objects.marketbridge.domains.order.controller.OrderReturnController;
import com.objects.marketbridge.domains.order.service.OrderCancelService;
import com.objects.marketbridge.domains.order.service.OrderReturnService;
import com.objects.marketbridge.domains.order.service.port.*;
import com.objects.marketbridge.domains.payment.service.port.PaymentClient;
import com.objects.marketbridge.domains.coupon.service.port.CouponRepository;
import com.objects.marketbridge.domains.coupon.service.port.MemberCouponRepository;
import com.objects.marketbridge.domains.product.mock.FakeProductRepository;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import lombok.Builder;

public class TestContainer {

    public final OrderCancelReturnController orderCancelReturnController;
    public final OrderCancelController orderCancelController;
    public final OrderReturnController orderReturnController;

    public final OrderCancelService orderCancelService;
    public final OrderReturnService orderReturnService;

    public final OrderQueryRepository orderQueryRepository;
    public final OrderCommandRepository orderCommandRepository;
    public final OrderDetailQueryRepository orderDetailQueryRepository;
    public final OrderDetailCommandRepository orderDetailCommandRepository;
    public final OrderCancelReturnQueryRepository orderCancelReturnQueryRepository;
    public final OrderCancelReturnCommandRepository orderCancelReturnCommandRepository;
    public final OrderDtoRepository orderDtoRepository;
    public final OrderDetailDtoRepository orderDetailDtoRepository;
    public final ProductRepository productRepository;
    public final CouponRepository couponRepository;
    public final MemberCouponRepository memberCouponRepository;
    public final MemberRepository memberRepository;
    public final PaymentClient paymentClient;
    public final CartQueryRepository cartQueryRepository;
    public final CartCommandRepository cartCommandRepository;

    @Builder
    public TestContainer(DateTimeHolder dateTimeHolder) {
        // Repository
        this.orderQueryRepository = new FakeOrderQueryRepository();
        this.orderCommandRepository = new FakeOrderCommandRepository();
        this.orderDetailQueryRepository = new FakeOrderDetailQueryRepository();
        this.orderDetailCommandRepository = new FakeOrderDetailCommandRepository();
        this.orderCancelReturnQueryRepository = new FakeOrderCancelReturnQueryRepository();
        this.orderCancelReturnCommandRepository = new FakeOrderCancelReturnCommandRepository();
        this.orderDtoRepository = new FakeOrderDtoRepository();
        this.orderDetailDtoRepository = new FakeOrderDetailDtoRepository();
        this.productRepository = new FakeProductRepository();
        this.couponRepository = new FakeCouponRepository();
        this.memberCouponRepository = new FakeMemberCouponRepository();
        this.memberRepository = new FakeMemberRepository();
        this.paymentClient = new FakePaymentClient(dateTimeHolder);
        this.cartQueryRepository = new FakeCartQueryRepository();
        this.cartCommandRepository = new FakeCartCommandRepository();

        // Service
        this.orderCancelService = OrderCancelService.builder()
                .orderDetailQueryRepository(this.orderDetailQueryRepository)
                .orderDetailCommandRepository(this.orderDetailCommandRepository)
                .orderCancelReturnQueryRepository(this.orderCancelReturnQueryRepository)
                .orderCancelReturnCommandRepository(this.orderCancelReturnCommandRepository)
                .paymentClient(this.paymentClient)
                .dateTimeHolder(dateTimeHolder)
                .build();
        this.orderReturnService = OrderReturnService.builder()
                .orderDetailQueryRepository(this.orderDetailQueryRepository)
                .orderDetailCommandRepository(this.orderDetailCommandRepository)
                .orderCancelReturnQueryRepository(this.orderCancelReturnQueryRepository)
                .orderCancelReturnCommandRepository(this.orderCancelReturnCommandRepository)
                .paymentClient(this.paymentClient)
                .dateTimeHolder(dateTimeHolder)
                .build();

        // Controller
        this.orderCancelReturnController = OrderCancelReturnController.builder()
                .orderDetailDtoRepository(this.orderDetailDtoRepository)
                .build();
        this.orderCancelController = OrderCancelController.builder()
                .orderCancelService(this.orderCancelService)
                .memberRepository(this.memberRepository)
                .dateTimeHolder(dateTimeHolder)
                .build();
        this.orderReturnController = OrderReturnController.builder()
                .orderReturnService(this.orderReturnService)
                .memberRepository(this.memberRepository)
                .dateTimeHolder(dateTimeHolder)
                .build();

    }
}

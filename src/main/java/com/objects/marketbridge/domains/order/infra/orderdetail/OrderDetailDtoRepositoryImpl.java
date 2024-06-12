package com.objects.marketbridge.domains.order.infra.orderdetail;

import com.objects.marketbridge.domains.order.domain.StatusCodeType;
import com.objects.marketbridge.domains.order.infra.dtio.GetCancelReturnListDtio;
import com.objects.marketbridge.domains.order.infra.dtio.QGetCancelReturnListDtio_OrderDetailInfo;
import com.objects.marketbridge.domains.order.infra.dtio.QGetCancelReturnListDtio_Response;
import com.objects.marketbridge.domains.order.infra.order.OrderJpaRepository;
import com.objects.marketbridge.domains.order.service.port.OrderDetailDtoRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.objects.marketbridge.domains.order.domain.QOrder.order;
import static com.objects.marketbridge.domains.order.domain.QOrderDetail.orderDetail;


@Slf4j
@Repository
@Transactional(readOnly = true)
public class OrderDetailDtoRepositoryImpl implements OrderDetailDtoRepository {

    private final OrderJpaRepository orderJpaRepository;

    private final JPAQueryFactory queryFactory;

    public OrderDetailDtoRepositoryImpl(OrderJpaRepository orderJpaRepository, EntityManager em) {
        this.orderJpaRepository = orderJpaRepository;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<GetCancelReturnListDtio.Response> findCancelReturnListDtio(Long memberId, Pageable pageable) {
        List<GetCancelReturnListDtio.Response> content = getOrderCancelReturnListResponses(memberId);

        JPAQuery<GetCancelReturnListDtio.Response> countQuery = getCountQuery(memberId);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    private List<GetCancelReturnListDtio.Response> getOrderCancelReturnListResponses(Long memberId) {
        return queryFactory
                .select(
                        new QGetCancelReturnListDtio_Response(
                                orderDetail.cancelledAt,
                                orderDetail.createdAt,
                                new QGetCancelReturnListDtio_OrderDetailInfo(
                                        orderDetail.orderNo,
                                        orderDetail.product.id,
                                        orderDetail.product.productNo,
                                        orderDetail.product.name,
                                        orderDetail.price,
                                        orderDetail.quantity,
                                        orderDetail.statusCode
                                )
                        )
                ).from(orderDetail)
                .join(orderDetail.order, order)
                .where(
                        eqMemberId(memberId),
                        eqStatusCode()
                )
                .fetch();
    }

    private JPAQuery<GetCancelReturnListDtio.Response> getCountQuery(Long memberId) {
        return queryFactory
                .select(
                        new QGetCancelReturnListDtio_Response(
                                orderDetail.cancelledAt,
                                orderDetail.createdAt,
                                new QGetCancelReturnListDtio_OrderDetailInfo(
                                        orderDetail.orderNo,
                                        orderDetail.product.id,
                                        orderDetail.product.productNo,
                                        orderDetail.product.name,
                                        orderDetail.price,
                                        orderDetail.quantity,
                                        orderDetail.statusCode
                                )
                        )
                ).from(orderDetail)
                .join(orderDetail.order, order)
                .where(
                        eqMemberId(memberId),
                        eqStatusCode()
                );
    }

    private BooleanExpression eqMemberId(Long memberId) {
        return order.member.id.eq(memberId);
    }

    private static BooleanExpression eqStatusCode() {
        return orderDetail.statusCode.eq(StatusCodeType.ORDER_CANCEL.getCode())
                .or(orderDetail.statusCode.eq(StatusCodeType.RETURN_COMPLETED.getCode()));
    }
}

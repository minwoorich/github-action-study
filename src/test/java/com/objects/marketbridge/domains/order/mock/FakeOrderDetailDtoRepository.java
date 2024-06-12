package com.objects.marketbridge.domains.order.mock;

import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.order.infra.dtio.GetCancelReturnListDtio;
import com.objects.marketbridge.domains.order.service.port.OrderDetailDtoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.function.Predicate;

import static com.objects.marketbridge.domains.order.domain.StatusCodeType.ORDER_CANCEL;
import static com.objects.marketbridge.domains.order.domain.StatusCodeType.RETURN_COMPLETED;

public class FakeOrderDetailDtoRepository extends BaseFakeOrderDetailRepository implements OrderDetailDtoRepository {

    @Override
    public Page<GetCancelReturnListDtio.Response> findCancelReturnListDtio(Long memberId, Pageable pageable) {
        List<GetCancelReturnListDtio.Response> content = getOrderCancelReturnListResponses(memberId);

        return PageableExecutionUtils.getPage(content, pageable, content::size);
    }

    private List<GetCancelReturnListDtio.Response> getOrderCancelReturnListResponses(Long memberId) {
        return getInstance().getData().stream()
                .filter(eqMemberId(memberId))
                .filter(statusCond())
                .map(FakeOrderDetailDtoRepository::createResponse)
                .toList();
    }

    private static Predicate<OrderDetail> eqMemberId(Long memberId) {
        return orderDetail -> orderDetail.getOrder().getMember().getId().equals(memberId);
    }

    private static Predicate<OrderDetail> statusCond() {
        return od -> od.getStatusCode().equals(ORDER_CANCEL.getCode())
                || od.getStatusCode().equals(RETURN_COMPLETED.getCode());
    }

    private static GetCancelReturnListDtio.Response createResponse(OrderDetail orderDetail) {
        return GetCancelReturnListDtio.Response.builder()
                .cancelReceiptDate(orderDetail.getCancelledAt())
                .orderDate(orderDetail.getCreatedAt())
                .orderDetailInfo(
                        GetCancelReturnListDtio.OrderDetailInfo.builder()
                                .orderNo(orderDetail.getOrderNo())
                                .productId(orderDetail.getProduct().getId())
                                .productNo(orderDetail.getProduct().getProductNo())
                                .name(orderDetail.getProduct().getName())
                                .price(orderDetail.getPrice())
                                .quantity(orderDetail.getQuantity())
                                .orderStatus(orderDetail.getStatusCode())
                                .build()
                )
                .build();
    }

}

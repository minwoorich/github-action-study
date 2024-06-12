package com.objects.marketbridge.domains.order.controller.dto.select;

import com.objects.marketbridge.domains.order.service.dto.GetOrderDetailDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderDetailInfo{
    private String orderNo;
    private Long orderDetailId;
    private Long productId;
    private Long quantity;
    private Long price;
    private String statusCode;
    private String deliveredDate; //yyyy-MM-dd HH:mm:ss
    private String productThumbImageUrl;
    private String productName;
    private List<String> optionNames;
    private Boolean isOwn;

    @Builder
    private OrderDetailInfo(String orderNo, Long orderDetailId, Long productId, Long quantity, Long price, String statusCode, String deliveredDate, String productThumbImageUrl, String productName, List<String> optionNames, Boolean isOwn) {
        this.orderNo = orderNo;
        this.orderDetailId = orderDetailId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.statusCode = statusCode;
        this.deliveredDate = deliveredDate;
        this.productThumbImageUrl = productThumbImageUrl;
        this.productName = productName;
        this.optionNames = optionNames;
        this.isOwn = isOwn;
    }

    // TODO : of() 에 deliveredDate 추가
    public static OrderDetailInfo of(GetOrderDetailDto getOrderDetailDto) {
        return OrderDetailInfo.builder()
                .orderNo(getOrderDetailDto.getOrderNo())
                .orderDetailId(getOrderDetailDto.getOrderDetailId())
                .productId(getOrderDetailDto.getProduct().getProductId())
                .quantity(getOrderDetailDto.getQuantity())
                .price(getOrderDetailDto.getPrice())
                .statusCode(getOrderDetailDto.getStatusCode())
                .deliveredDate("2024-01-01 12:00:00") //yyyy-MM-dd HH:mm:ss
                .productThumbImageUrl(getOrderDetailDto.getProduct().getThumbImg())
                .productName(getOrderDetailDto.getProduct().getName())
                .optionNames(getOrderDetailDto.getProduct().getOptionNames())
                .isOwn(getOrderDetailDto.getProduct().getIsOwn())
                .build();
    }
}

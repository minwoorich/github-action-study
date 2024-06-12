package com.objects.marketbridge.domains.order.service.dto;

import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.product.domain.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class GetOrderDetailDto {

    private Long orderDetailId;
    private ProductDto product;
    private Long quantity;
    private String orderNo;
    private Long price;
    private String statusCode;
//    private LocalDateTime deliveredDate;
    private LocalDateTime cancelledAt;

    @Builder
    public GetOrderDetailDto(Long orderDetailId, ProductDto product, Long quantity, String orderNo, Long price, String statusCode, LocalDateTime cancelledAt) {
        this.orderDetailId = orderDetailId;
        this.product = product;
        this.quantity = quantity;
        this.orderNo = orderNo;
        this.price = price;
        this.statusCode = statusCode;
        this.cancelledAt = cancelledAt;
    }

    public static GetOrderDetailDto of(OrderDetail orderDetail) {
        return GetOrderDetailDto.builder()
                .orderDetailId(orderDetail.getId())
                .quantity(orderDetail.getQuantity())
                .orderNo(orderDetail.getOrderNo())
                .price(orderDetail.getPrice())
                .statusCode(orderDetail.getStatusCode())
                .cancelledAt(orderDetail.getCancelledAt())
                .product(ProductDto.of(orderDetail.getProduct()))
                .build();
    }

    public static GetOrderDetailDto create(Long orderDetailId, ProductDto product, Long quantity, String orderNo, Long price, String statusCode, LocalDateTime cancelledAt) {
        return GetOrderDetailDto.builder()
                .orderDetailId(orderDetailId)
                .product(product)
                .quantity(quantity)
                .orderNo(orderNo)
                .price(price)
                .statusCode(statusCode)
                .cancelledAt(cancelledAt)
                .build();

    }

    @Getter
    @NoArgsConstructor
    public static class ProductDto{

        private Long productId;
        private List<String> optionNames;
        private Boolean isOwn; // 로켓 true , 오픈 마켓 false
        private String name;
        private Long price;
        private String thumbImg;
        private String productNo;

        @Builder
        private ProductDto(Long productId, List<String> optionNames, Boolean isOwn, String name, Long price, String thumbImg, String productNo) {
            this.productId = productId;
            this.optionNames = optionNames;
            this.isOwn = isOwn;
            this.name = name;
            this.price = price;
            this.thumbImg = thumbImg;
            this.productNo = productNo;
        }

        public static ProductDto of(Product product) {
            return ProductDto.builder()
                    .productId(product.getId())
                    .optionNames(product.getProdOptions().stream().map(po -> po.getOption().getName()).collect(Collectors.toList()))
                    .isOwn(product.getIsOwn())
                    .name(product.getName())
                    .price(product.getPrice())
                    .thumbImg(product.getThumbImg())
                    .productNo(product.getProductNo())
                    .build();
        }

        public static ProductDto create(Long productId, List<String> optionNames, Boolean isOwn, String name, Long price, String thumbImg, String productNo) {
            return ProductDto.builder()
                    .productId(productId)
                    .optionNames(optionNames)
                    .isOwn(isOwn)
                    .name(name)
                    .price(price)
                    .thumbImg(thumbImg)
                    .productNo(productNo)
                    .build();
        }
    }
}

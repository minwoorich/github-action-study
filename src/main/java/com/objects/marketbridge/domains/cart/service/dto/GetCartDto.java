package com.objects.marketbridge.domains.cart.service.dto;

import com.objects.marketbridge.domains.cart.domain.Cart;
import com.objects.marketbridge.domains.product.domain.ProdOption;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@NoArgsConstructor
@Slf4j
public class GetCartDto {
    private Long cartId;
    private Long productId;
    private String productNo;
    private String productName;
    private Long productPrice;
    private Long quantity;
    private Long discountRate;
    private String thumbImageUrl;
    private Boolean isOwn;
    private Boolean isSubs;
    private Long stock;
    private Long deliveryFee;
    private String deliveredDate; // yyyy-MM-dd HH:mm:ss
    private List<String> optionNames;

    @Builder
    private GetCartDto(Long cartId, Long productId, String productNo, String productName, Long productPrice, Long quantity, Long discountRate, String thumbImageUrl, Boolean isOwn, Boolean isSubs, Long stock, Long deliveryFee, String deliveredDate, List<String> optionNames) {
        this.cartId = cartId;
        this.productId = productId;
        this.productNo = productNo;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.discountRate = discountRate;
        this.thumbImageUrl = thumbImageUrl;
        this.isOwn = isOwn;
        this.isSubs = isSubs;
        this.stock = stock;
        this.deliveryFee = deliveryFee;
        this.deliveredDate = deliveredDate;
        this.optionNames = optionNames;
    }

    public static GetCartDto of(Cart cart) {
        List<ProdOption> prodOptions = cart.getProduct().getProdOptions();

        return GetCartDto.builder()
                .cartId(cart.getId())
                .productId(cart.getProduct().getId())
                .productNo(cart.getProduct().getProductNo())
                .productName(cart.getProduct().getName())
                .productPrice(cart.getProduct().getPrice())
                .quantity(cart.getQuantity())
                .discountRate(cart.getProduct().getDiscountRate())
                .thumbImageUrl(cart.getProduct().getThumbImg())
                .isOwn(cart.getProduct().getIsOwn())
                .isSubs(cart.getProduct().getIsSubs())
                .stock(cart.getProduct().getStock())
                .deliveryFee(0L)
                .deliveredDate("2024-01-01 00:00:00")
                .optionNames(!prodOptions.isEmpty() ?
                        prodOptions.stream().map(po -> po.getOption().getName()).collect(toList()) : null)

                .build();
    }
}

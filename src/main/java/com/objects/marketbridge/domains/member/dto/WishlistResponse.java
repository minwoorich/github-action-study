package com.objects.marketbridge.domains.member.dto;

import com.objects.marketbridge.domains.member.domain.Wishlist;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WishlistResponse {

    private Long productId;
    private List<String> optionNameList;
    private Long price;
    private String productName;
    private String thumbImgUrl;
    private Boolean isSoldOut;

    @Builder
    public WishlistResponse(Long productId, List<String> optionNameList, Long price, String productName, String thumbImgUrl, Boolean isSoldOut) {
        this.productId = productId;
        this.optionNameList = optionNameList;
        this.price = price;
        this.productName = productName;
        this.thumbImgUrl = thumbImgUrl;
        this.isSoldOut = isSoldOut;
    }

    public static WishlistResponse of(Wishlist wishlist){
        return  WishlistResponse.builder()
                .productId(wishlist.getProduct().getId())
                .optionNameList(wishlist.getProduct().getProdOptions().stream().map(po->po.getOption().getName()).collect(Collectors.toList()))
                .price(wishlist.getProduct().getPrice())
                .productName(wishlist.getProduct().getName())
                .thumbImgUrl(wishlist.getProduct().getThumbImg())
                .isSoldOut(wishlist.getProduct().getStock() <= 0)
                .build();
    }
}

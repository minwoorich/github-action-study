package com.objects.marketbridge.domains.product.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class CreateProductRequestDto {
    @NotNull
    private Long categoryId;

    @NotNull
    private Boolean isOwn; // 로켓 true , 오픈 마켓 false
    @NotNull
    private String name;
    @NotNull
    private Long price;
    @NotNull
    private Boolean isSubs;
    @NotNull
    private Long stock;

    private String thumbImg;

    private List<String> itemImgUrls = new ArrayList<>();
    private List<String> detailImgUrls = new ArrayList<>();

    @NotNull
    private Long discountRate;

    private String productNo;

    private Map<String,String> optionInfo =  new HashMap<>();

    private Map<String,String> tagInfo = new HashMap<>();


    @Builder
    public CreateProductRequestDto(Long categoryId, Boolean isOwn, String name, Long price, Boolean isSubs, Long stock, String thumbImg, List<String> itemImgUrls, List<String> detailImgUrls, Long discountRate, String productNo, Map<String,String> optionInfo, Map<String,String> tagInfo) {
        this.categoryId = categoryId;
        this.isOwn = isOwn;
        this.name = name;
        this.price = price;
        this.isSubs = isSubs;
        this.stock = stock;
        this.thumbImg = thumbImg;
        this.itemImgUrls = itemImgUrls;
        this.detailImgUrls = detailImgUrls;
        this.discountRate = discountRate;
        this.optionInfo = optionInfo;
        this.productNo = productNo;
        this.tagInfo = tagInfo;
    }
}

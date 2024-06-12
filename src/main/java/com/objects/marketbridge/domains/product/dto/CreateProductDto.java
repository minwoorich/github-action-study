package com.objects.marketbridge.domains.product.dto;

import com.objects.marketbridge.domains.image.domain.ImageType;
import com.objects.marketbridge.domains.product.controller.request.CreateProductRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Getter
public class CreateProductDto {

    private Long categoryId;
    private Boolean isOwn; // 로켓 true , 오픈 마켓 false
    private String name;
    private Long price;
    private Boolean isSubs;
    private Long stock;
    private String thumbImg;
    private Long discountRate;
    private String productNo;
    private List<ProductImageDto> productImageList;
    private List<OptionDto> optionInfoList;
    private List<ProdTagDto> prodTagList;

    @Builder
    public CreateProductDto(Long categoryId, Boolean isOwn, String name, Long price, Boolean isSubs, Long stock, String thumbImg, Long discountRate, String productNo,List<ProductImageDto> productImageList,List<OptionDto> optionInfoList,List<ProdTagDto> prodTagList) {
        this.categoryId = categoryId;
        this.isOwn = isOwn;
        this.name = name;
        this.price = price;
        this.isSubs = isSubs;
        this.stock = stock;
        this.thumbImg = thumbImg;
        this.discountRate = discountRate;
        this.productNo = productNo;
        this.productImageList = productImageList;
        this.optionInfoList = optionInfoList;
        this.prodTagList = prodTagList;
    }

    public static CreateProductDto fromRequest(CreateProductRequestDto request){
        List<ProductImageDto> productImageList= toProductImageDto(request.getItemImgUrls(), ImageType.ITEM_IMG.toString());
        productImageList.addAll(toProductImageDto(request.getDetailImgUrls(), ImageType.DETAIL_IMG.toString()));

        return CreateProductDto.builder()
                .categoryId(request.getCategoryId())
                .isOwn(request.getIsOwn())
                .name(request.getName())
                .price(request.getPrice())
                .isSubs(request.getIsSubs())
                .stock(request.getStock())
                .thumbImg(request.getThumbImg())
                .discountRate(request.getDiscountRate())
                .productNo(request.getProductNo())
                .productImageList(productImageList)
                .prodTagList(toProdTagDto(request.getTagInfo()))
                .optionInfoList(toOptionDto(request.getOptionInfo()))
                .build();
    }

    private static List<ProdTagDto> toProdTagDto(Map<String,String> tagInfo){
        List<ProdTagDto> prodTagDtos = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(tagInfo);

        Iterator<String> keys = jsonObject.keySet().iterator();
        while (keys.hasNext()){
            String k = keys.next();
            prodTagDtos.add(new ProdTagDto().create(k,jsonObject.getString(k)));
        }
        return prodTagDtos;
    }

    private static List<OptionDto> toOptionDto(Map<String,String> optionInfo){
        List<OptionDto> optionDtos = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(optionInfo);

        Iterator<String> keys = jsonObject.keySet().iterator();
        while (keys.hasNext()){
            String k = keys.next();
            optionDtos.add(new OptionDto().create(k,jsonObject.getString(k)));
        }

        return optionDtos;
    }

    private static List<ProductImageDto> toProductImageDto(List<String> imgUrls,String type){
        List<ProductImageDto> productImageDtos = new ArrayList<>();
        for (int i = 0; i < imgUrls.size(); i++) {
            productImageDtos.add(new ProductImageDto().create(imgUrls.get(i),type,Long.valueOf(i)));
        }
        return productImageDtos;
    }
}

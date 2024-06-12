package com.objects.marketbridge.domains.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Builder
public class ProductSearchConditionDto {

    @Builder.Default
    private final List<Long> leafDepthInfoList = new ArrayList<>();

    public void addLeafDepthInfoList(Long leafDepthInfo){
        leafDepthInfoList.add(leafDepthInfo);
    }
}

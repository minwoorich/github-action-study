package com.objects.marketbridge.common.utils;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.domain.Sort;

public class MyQueryDslUtils {
    public static  OrderSpecifier<?> createOrderSpecifier(Sort.Order order, Path<?> parent, String fieldName) {

        // 1. 내림차순 or 오름차순 구분
        Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

        // 2. OrderSpecifier 를 생성하는데 필요한 'Path' 객체 생성. (Path : 쉽게말해 QMember와 QMember의 필드)
        Path<Object> fieldPath = Expressions.path(Object.class, parent, fieldName);

        return new OrderSpecifier(direction, fieldPath);
    }
}

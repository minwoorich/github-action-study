package com.objects.marketbridge.domains.order.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusCodeType {

    /**
     * INIT         00
     * PENDING      01
     * RECEIVED     02
     * ING          03
     * COMPLETED    04
     * CANCEL       05
     * DELAY        06
     * FAILURE      07
     * FAULT        08
     * DISPOSAL     09
     * RETURN       10
     */
    ORDER_INIT("주문생성", "OR00"),
    ORDER_RECEIVED("주문접수", "OR01"),
    ORDER_CANCEL("주문취소", "OR05"),
    ORDER_RETURN("주문반품", "OR10"),

    ORDER_PARTIAL_CANCEL("부분취소", "OP05"),
    ORDER_PARTIAL_RETURN("부분반품", "OP10"),

    PAYMENT_PENDING("결제대기", "PM01"),
    PAYMENT_COMPLETED("결제완료", "PM04"),
    PAYMENT_FAILURE("결제실패","PM07"),
    PAYMENT_CANCEL("결제취소","PM05"),

    DELIVERY_PENDING("배송대기","DL01"),
    DELIVERY_ING("배송중","DL03"),
    DELIVERY_COMPLETED("배송완료","DL04"),
    DELIVERY_DELAY("배송지연","DL06"),

    RELEASE_PENDING("출고대기","RE01"),
    RELEASE_COMPLETED("출고완료","RE04"),

    RETURN_INIT("반품접수","RT00"),
    RETURN_CANCEL("반품취소","RT05"),
    RETURN_COMPLETED("반품완료","RT04"),

    RECALL_ING("회수중","RC03"),
    RECALL_COMPLETED("회수완료", "RC04"),

    INSPECTION_ING("검수중", "IP03"),
    INSPECTION_COMPLETED("검수완료", "IP04"),

    SELLER_FAULT("판매자과실", "SL08"),

    PRODUCT_DISPOSAL("제품폐기", "PR09"),

    CUSTOMER_FAULT("고객과실", "CS08");

    private final String text;
    private final String code;
}

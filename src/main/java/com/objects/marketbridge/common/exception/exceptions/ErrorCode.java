package com.objects.marketbridge.common.exception.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_INPUT_VALUE("유효하지 않은 입력 값입니다."),
    INVALID_TOTAL_AMOUNT("유효하지 않은 주문 금액입니다."),
    INVALID_PAYMENT_AMOUNT("유효하지 않은 액수입니다."),
    MISMATCHED_PAYMENT_DATA("결제정보 검증에 실패하였습니다."),
    SHIPPING_ADDRESS_NOT_REGISTERED("등록된 배송지 정보가 없습니다."),
    DEFAULT_ADDRESS_DELETION_NOT_ALLOWED("기본 배송지는 삭제할 수 없습니다."),
    PAYMENT_NOT_FOUND("결제 정보를 찾을 수 없습니다."),
    OUT_OF_STOCK("재고가 없습니다."),
    SESSION_EXPIRED("세션값이 만료되었습니다."),
    COUPON_EXPIRED("만료된 쿠폰입니다."),
    COUPON_INCOMPATIBLE("상품과 호환되지 않는 쿠폰입니다."),
    COUPON_CONDITION_VIOLATION("최소 주문 금액 조건 미달입니다."),
    COUPON_ALREADY_USED("이미 사용한 쿠폰입니다."),
    COUPON_OUT_OF_STOCK("쿠폰이 모두 소진되었습니다"),
    BALANCE_INSUFFICIENT("잔액이 부족합니다."),
    METHOD_NOT_ALLOWED("지원하지않는 HTTP 메서드 호출입니다."),
    RESOUCRE_NOT_FOUND("리소스를 찾을 수 없습니다."),
    ORDERDETAIL_NOT_FOUND("주문 상세 정보를 찾을 수 없습니다."),
    ORDER_NOT_FOUND("주문 정보를 찾을 수 없습니다."),
    QUANTITY_EXCEEDED("수량이 초과 되었습니다."),
    NON_CANCELLABLE_PRODUCT("취소가 불가능한 상품입니다."),
    NON_RETURNABLE_PRODUCT("반품이 불가능한 상품입니다."),
    NON_CANCELLABLE_RETURNABLE_PRODUCT("취소/반품이 불가능한 상품입니다."),
    DUPLICATE_OPERATION("이미 완료되었습니다."),
    EXTERNAL_API_FAILURE("외부 API 호출에 실패하였습니다."),
    DUPLICATE_ERROR("이미 존재합니다."),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_FOUND("회원 정보가 존재하지 않습니다."),
    NO_ERROR_CODE("빈 에러코드."),
    EXPIRED_JWT_ERROR("토큰이 만료되었습니다."),
    MALFORMED_JWT_ERROR("JWT 토큰이 아닙니다."),
    SIGNATURE_JWT_ERROR("잘못된 형식의 토큰입니다."),
    BAD_CREDENTIALS_ERROR("로그인 정보가 일치하지 않습니다."),
    UNSUPPORTED_JWT_ERROR(""),
    INTERNAL_SECURITY_ERROR("자격 증명에 실패하였습니다.");


    private final String message;
}

package com.objects.marketbridge.domains.cart.service;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.domains.cart.domain.Cart;
import com.objects.marketbridge.domains.cart.service.dto.CreateCartDto;
import com.objects.marketbridge.domains.cart.service.port.CartCommandRepository;
import com.objects.marketbridge.domains.cart.service.port.CartQueryRepository;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.product.domain.Product;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.DUPLICATE_OPERATION;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddToCartService {

    private final CartCommandRepository cartCommandRepository;
    private final CartQueryRepository cartQueryRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Cart add(CreateCartDto createCartDto) {
        // 1. 이미 장바구니에 담긴 상품인지 아닌지 검증
        validDuplicate(createCartDto.getProductId(), createCartDto.getMemberId());

        return cartCommandRepository.save(create(createCartDto));
    }

    private Cart create(CreateCartDto createCartDto) {
        Product product = productRepository.findById(createCartDto.getProductId());
        Member member = memberRepository.findById(createCartDto.getMemberId());
        Boolean isSubs = createCartDto.getIsSubs();
        Long quantity = createCartDto.getQuantity();

        return Cart.create(member, product, isSubs, quantity);
    }
    private void validDuplicate(Long productId, Long memberId) {
        if (cartQueryRepository.findByProductIdAndMemberId(productId, memberId).isPresent()) {
            throw CustomLogicException.createBadRequestError(DUPLICATE_OPERATION, "이미 장바구니에 담긴 상품입니다");
        }
    }
}

package com.objects.marketbridge.domains.cart.service;

import com.objects.marketbridge.domains.cart.domain.Cart;
import com.objects.marketbridge.domains.cart.service.dto.UpdateCartDto;
import com.objects.marketbridge.domains.cart.service.port.CartQueryRepository;
import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.exception.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UpdateCartService {

    private final CartQueryRepository cartQueryRepository;
    public void update(UpdateCartDto dto) {
        Cart cart = null;
        try {
            cart = cartQueryRepository.findById(dto.getCartId());
        } catch (JpaObjectRetrievalFailureException e) {
            throw CustomLogicException.createBadRequestError(ErrorCode.RESOUCRE_NOT_FOUND, "해당 장바구니 항목이 존재하지 않습니다", LocalDateTime.now());
        }
        cart.updateQuantity(dto.getQuantity());
    }
}

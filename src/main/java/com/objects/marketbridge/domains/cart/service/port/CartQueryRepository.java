package com.objects.marketbridge.domains.cart.service.port;

import com.objects.marketbridge.domains.cart.domain.Cart;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface CartQueryRepository {

    Optional<Cart> findByProductIdAndMemberId(Long productId, Long memberId);

    Cart findById(Long id);

    Slice<Cart> findSlicedCart(Pageable pageable, Long memberId);

    List<Cart> findAll();

    Long countByMemberId(Long memberId);

}

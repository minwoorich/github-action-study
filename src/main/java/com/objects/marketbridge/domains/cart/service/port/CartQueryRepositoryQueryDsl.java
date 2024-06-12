package com.objects.marketbridge.domains.cart.service.port;

import com.objects.marketbridge.domains.cart.domain.Cart;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CartQueryRepositoryQueryDsl {

    Slice<Cart> findSlicedCart(Pageable pageable, Long memberId);

}

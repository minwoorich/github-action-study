package com.objects.marketbridge.domains.cart.service.port;

import com.objects.marketbridge.domains.cart.domain.Cart;

import java.util.List;

public interface CartCommandRepository {

    Cart save(Cart cart);

    void saveAll(List<Cart> carts);

    void deleteAllInBatch();

    void deleteAllByIdInBatch(List<Long> ids);

    void saveAndFlush(Cart cart);

    void deleteAllByProductIdsAndMemberIdInBatch(List<Long> productIds, Long memberId);

}

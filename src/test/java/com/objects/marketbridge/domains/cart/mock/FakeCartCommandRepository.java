package com.objects.marketbridge.domains.cart.mock;

import com.objects.marketbridge.domains.cart.domain.Cart;
import com.objects.marketbridge.domains.cart.service.port.CartCommandRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Objects;

public class FakeCartCommandRepository extends BaseFakeCartRepository implements CartCommandRepository {
    @Override
    public Cart save(Cart cart) {
        if (cart.getId() == null || cart.getId() == 0) {
            ReflectionTestUtils.setField(cart, "id", increaseId(), Long.class);
        } else {
            getInstance().getData().removeIf(item -> Objects.equals(item.getId(), cart.getId()));
        }
        getInstance().getData().add(cart);
        return cart;
    }

    @Override
    public void saveAll(List<Cart> carts) {
        carts.forEach(cart -> {
            if (cart.getId() == null || cart.getId() == 0L) {
                ReflectionTestUtils.setField(cart, "id", increaseId(), Long.class);
            } else{
                getInstance().getData().removeIf(item -> Objects.equals(item.getId(), cart.getId()));
            }
            getInstance().getData().add(cart);
        });
    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {

    }

    @Override
    public void saveAndFlush(Cart cart) {

    }

    @Override
    public void deleteAllByProductIdsAndMemberIdInBatch(List<Long> productIds, Long memberId) {
        List<Cart> carts = getInstance().getData();
        List<Cart> memberCarts = carts.stream().filter(c -> Objects.equals(c.getMember().getId(), memberId)).toList();
        List<Cart> deletedCartList = memberCarts.stream().filter(c -> productIds.contains(c.getProduct().getId())).toList();

        getInstance().getData().removeAll(deletedCartList);

    }
}

package com.objects.marketbridge.domains.cart.mock;

import com.objects.marketbridge.domains.cart.domain.Cart;
import com.objects.marketbridge.domains.cart.service.port.CartQueryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FakeCartQueryRepository extends BaseFakeCartRepository implements CartQueryRepository {
    @Override
    public Cart findById(Long id) {
        return getInstance().getData().stream()
                .filter(cart -> Objects.equals(id, cart.getId()))
                .findAny()
                .orElseThrow(() -> new JpaObjectRetrievalFailureException(new EntityNotFoundException()));
    }

    @Override
    public List<Cart> findAll() {
        return getInstance().getData();
    }

    @Override
    public Optional<Cart> findByProductIdAndMemberId(Long productId, Long memberId) {
        return getInstance().getData().stream()
                .filter(cart -> Objects.equals(productId, cart.getProduct().getId()))
                .filter(cart -> Objects.equals(memberId, cart.getMember().getId()))
                .findAny();
    }

    @Override
    public Long countByMemberId(Long memberId) {
        return getInstance().getData().stream()
                .filter(cart -> Objects.equals(cart.getMember().getId(), memberId))
                .count();
    }

    @Override
    public Slice<Cart> findSlicedCart(Pageable pageable, Long memberId) {
        throw new IllegalArgumentException("findSlicedCart()는 아직 구현이 되지 않았습니다");
    }
}

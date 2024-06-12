package com.objects.marketbridge.domains.cart.infra;

import com.objects.marketbridge.domains.cart.domain.Cart;
import com.objects.marketbridge.domains.cart.service.port.CartQueryRepository;
import com.objects.marketbridge.domains.cart.service.port.CartQueryRepositoryQueryDsl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CartQueryRepositoryImpl implements CartQueryRepository {

    private final CartJpaRepository cartJpaRepository;
    private final CartQueryRepositoryQueryDsl cartQueryRepositoryQueryDsl;

    @Override
    public List<Cart> findAll() {
        return cartJpaRepository.findAll();
    }

    @Override
    public Optional<Cart> findByProductIdAndMemberId(Long productId, Long memberId) {
        return cartJpaRepository.findByProductIdAndMemberId(productId, memberId);
    }

    @Override
    public Cart findById(Long id) {
        return cartJpaRepository.findById(id).orElseThrow(() -> new JpaObjectRetrievalFailureException(new EntityNotFoundException()));
    }

    @Override
    public Slice<Cart> findSlicedCart(Pageable pageable, Long memberId) {
        return cartQueryRepositoryQueryDsl.findSlicedCart(pageable, memberId);
    }

    @Override
    public Long countByMemberId(Long memberId) {
        return cartJpaRepository.countByMemberId(memberId);
    }
}

package com.objects.marketbridge.domains.cart.infra;

import com.objects.marketbridge.domains.cart.domain.Cart;
import com.objects.marketbridge.domains.cart.service.port.CartQueryRepositoryQueryDsl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.objects.marketbridge.domains.cart.domain.QCart.cart;
import static com.objects.marketbridge.domains.member.domain.QMember.member;
import static com.objects.marketbridge.domains.product.domain.QProduct.product;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CartQueryRepositoryQueryDslImpl implements CartQueryRepositoryQueryDsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Cart> findSlicedCart(Pageable pageable, Long memberId) {
        int pageSize = pageable.getPageSize();
        List<Cart> contents = queryFactory
                .selectFrom(cart)
                .join(cart.member, member).fetchJoin()
                .join(cart.product, product).fetchJoin()
                .where(eqMemberId(memberId))
                .offset(pageable.getOffset())
                .limit(pageSize + 1)
                .orderBy(cart.createdAt.desc())
                .fetch();

        boolean hasNext = false;
        if (contents.size() > pageSize) {
            contents.remove(pageSize);
            hasNext = true;
        }

        // Slice 객체 반환
        return new SliceImpl<>(contents, pageable, hasNext);
    }

    private BooleanExpression eqMemberId(Long memberId) {
        return cart.member.id.eq(memberId);
    }
}

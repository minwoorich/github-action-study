package com.objects.marketbridge.domains.cart.infra;

import com.objects.marketbridge.domains.cart.service.port.CartDtoRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class CartDtoRepositoryImpl implements CartDtoRepository {

    private final JPAQueryFactory queryFactory;

    public CartDtoRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }



}

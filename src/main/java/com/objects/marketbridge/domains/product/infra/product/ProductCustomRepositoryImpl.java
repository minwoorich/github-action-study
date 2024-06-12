package com.objects.marketbridge.domains.product.infra.product;

import com.objects.marketbridge.domains.category.domain.QCategory;
import com.objects.marketbridge.domains.product.domain.Product;
import com.objects.marketbridge.domains.product.domain.QProduct;
import com.objects.marketbridge.domains.product.service.port.ProductCustomRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Product findByIdwithCategory(Long id) {
        QCategory category = new QCategory("category");
        QProduct product = new QProduct("product");

        Product findProduct = queryFactory
                .select(product)
                .from(product)
                .leftJoin(product.category, category).fetchJoin()
                .where(product.id.eq(id))
                .fetchOne();

        if (findProduct == null){
            return Product.builder().name("EMPTY").build();
        }

        return findProduct;
    }

    @Override
    public List<Product> findAllByProductNoLikeAndProductId(String productNo, Long productId) {
        QProduct product = new QProduct("product");

        List<Product> result = queryFactory
                .selectFrom(product)
                .where(
                        product.productNo.like(productNo+"%"),
                        product.id.ne(productId)
                ).fetch();

        if (result == null){
            return new ArrayList<>();
        }

        return result;
    }
}

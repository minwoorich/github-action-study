package com.objects.marketbridge.domains.product.infra.productImage;

import com.objects.marketbridge.domains.product.dto.ProductImageDto;
import com.objects.marketbridge.domains.image.domain.QImage;
import com.objects.marketbridge.domains.product.domain.QProductImage;
import com.objects.marketbridge.domains.product.service.port.ProductImageCustomRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductImageCustomRepositoryImpl implements ProductImageCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProductImageDto> findAllByProductIdWithImage(Long productId) {
        QProductImage pi = new QProductImage("pi");
        QImage i = new QImage("i");

        List<ProductImageDto> result = queryFactory
                .select(Projections.fields(ProductImageDto.class,
                        pi.imgType.as("type"),
                        pi.seqNo,
                        i.url.as("imgUrl")
                        )
                ).from(pi)
                .leftJoin(i).on(pi.image.eq(i))
                .where(pi.product.id.eq(productId)).fetch();

        if (result == null){
            return new ArrayList<>();
        }

        return result;
    }
}

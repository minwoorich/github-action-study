package com.objects.marketbridge.domains.product.infra.tag;

import com.objects.marketbridge.domains.product.domain.QProdTag;
import com.objects.marketbridge.domains.product.domain.QTag;
import com.objects.marketbridge.domains.product.domain.QTagCategory;
import com.objects.marketbridge.domains.product.dto.ProdTagDto;
import com.objects.marketbridge.domains.product.service.port.ProdTagCustomRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProdTagCustomRepositoryImpl implements ProdTagCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProdTagDto> findAllByProductId(Long productId) {
        QProdTag pt = new QProdTag("pt");
        QTag t = new QTag("t");
        QTagCategory tc = new QTagCategory("tc");

        List<ProdTagDto> result = queryFactory
                .select(Projections.fields(ProdTagDto.class,
                        tc.name.as("tagKey"),
                        t.name.as("tagValue")
                        )
                ).from(pt)
                .leftJoin(t).on(pt.tag.eq(t))
                .leftJoin(tc).on(t.tagCategory.eq(tc))
                .where(pt.product.id.eq(productId)).fetch();

        if (result == null){
            return new ArrayList<>();
        }

        return result;
    }
}

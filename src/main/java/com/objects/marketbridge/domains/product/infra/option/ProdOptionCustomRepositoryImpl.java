package com.objects.marketbridge.domains.product.infra.option;

import com.objects.marketbridge.domains.product.domain.QOption;
import com.objects.marketbridge.domains.product.domain.QOptionCategory;
import com.objects.marketbridge.domains.product.domain.QProdOption;
import com.objects.marketbridge.domains.product.dto.OptionDto;
import com.objects.marketbridge.domains.product.service.port.ProdOptionCustomRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProdOptionCustomRepositoryImpl implements ProdOptionCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<OptionDto> findAllByProductId(Long id) {
        QProdOption po = new QProdOption("po");
        QOption o = new QOption("o");
        QOptionCategory oc = new QOptionCategory("oc");

        List<OptionDto> result =queryFactory
                .select(Projections.fields(OptionDto.class,
                        oc.name.as("optionCategory"),
                        o.name
                        )
                ).from(po)
                .leftJoin(o).on(po.option.eq(o))
                .leftJoin(oc).on(o.optionCategory.eq(oc))
                .where(po.product.id.eq(id)).fetch();

        if (result == null){
            return new ArrayList<>();
        }

        return result;
    }
}

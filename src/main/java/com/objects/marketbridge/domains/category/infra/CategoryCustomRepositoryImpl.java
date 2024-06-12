package com.objects.marketbridge.domains.category.infra;

import com.objects.marketbridge.domains.category.domain.Category;
import com.objects.marketbridge.domains.category.domain.QCategory;
import com.objects.marketbridge.domains.category.dto.CategoryDto;
import com.objects.marketbridge.domains.category.service.port.CategoryCustomRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryCustomRepositoryImpl implements CategoryCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Category> findAllOrderByParentIdAscNullsFirstCategoryIdAsc() {
        QCategory c1 = new QCategory("c1");
        QCategory c2 = new QCategory("c2");


        List<Category> result = queryFactory
                .select(Projections.fields(Category.class,
                        c1.id,
                        c2.as("parent"),
                        c1.level,
                        c1.name
                        )
                )
                .from(c1)
                .leftJoin(c2).on(c2.eq(c1.parent),c1.id.ne(c1.parent.id))
                .orderBy(c2.id.asc(),c1.id.asc())
                .fetch();

        if (result == null){
            return new ArrayList<>();
        }

        return result;
    }

    @Override
    public String findByChildId(Long categoryId) {
        QCategory c1 = new QCategory("c1");
        QCategory c2 = new QCategory("c2");
        QCategory c3 = new QCategory("c3");

        List<String> result = new ArrayList<>();

        Tuple tuple = queryFactory
                .select(c3.name, c2.name, c1.name)
                .from(c1)
                .leftJoin(c2).on(
                        c2.eq(c1.parent)
                                .and(c1.ne(c1.parent)))
                .leftJoin(c3).on(
                        c3.eq(c2.parent)
                                .and(c2.ne(c2.parent)))
                .where(
                        c1.id.eq(categoryId)
                ).fetchOne();
        for (int i = 0; i < tuple.size(); i++) {
            if (tuple.get(i,String.class) != null){
                result.add(tuple.get(i,String.class));
            }
        }

        return String.join(">",result);
    }
}

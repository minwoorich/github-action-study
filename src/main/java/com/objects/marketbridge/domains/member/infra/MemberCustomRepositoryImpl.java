package com.objects.marketbridge.domains.member.infra;

import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.domain.QAddress;
import com.objects.marketbridge.domains.member.domain.QMember;
import com.objects.marketbridge.domains.member.service.port.MemberCustomRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Member findByIdWithAddresses(Long id) {
        QMember member = new QMember("member");
        QAddress address = new QAddress("address");

        Member findMember = queryFactory
                .select(member)
                .from(member)
                .leftJoin(member.addresses, address).fetchJoin() // 일대다 관계 fetchJoin
                .where(member.id.eq(id))
                .fetchOne();

        if (findMember == null) {
            throw new EntityNotFoundException("엔티티가 존재하지 않습니다");
        }

        return findMember;

    }
}

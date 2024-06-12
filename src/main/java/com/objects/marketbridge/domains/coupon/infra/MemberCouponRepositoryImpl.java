package com.objects.marketbridge.domains.coupon.infra;

import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.coupon.service.port.MemberCouponRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberCouponRepositoryImpl implements MemberCouponRepository {

    private final MemberCouponJpaRepository memberCouponJpaRepository;

    @Override
    public MemberCoupon findById(Long id) {
        return memberCouponJpaRepository.findById(id)
                .orElseThrow(() -> new JpaObjectRetrievalFailureException(new EntityNotFoundException("해당하는 MemberCoupon 엔티티가 존재하지 않습니다. 입력 id = "+id)));
    }

    @Override
    public List<MemberCoupon> findByMemberId(Long memberId) {
        return memberCouponJpaRepository.findByMemberId(memberId);
    }

    @Override
    public MemberCoupon findByMemberIdAndCouponId(Long memberId, Long couponId) {
        return memberCouponJpaRepository.findByMemberIdAndCouponId(memberId, couponId)
                .orElseThrow(() -> new JpaObjectRetrievalFailureException(new EntityNotFoundException("해당하는 MemberCoupon 엔티티가 존재하지 않습니다. 입력 (memberId, coponId) = ("+memberId+", "+couponId+")")));
    }

    @Override
    public Optional<MemberCoupon> findByMemberIdAndCouponIdOptional(Long memberId, Long couponId) {
        return memberCouponJpaRepository.findByMemberIdAndCouponId(memberId, couponId);
    }

    @Override
    public MemberCoupon findByMemberIdAndCouponIdAndProductId(Long memberId, Long couponId, Long productId) {
        return memberCouponJpaRepository.findByMemberIdAndCouponIdAndProductId(memberId, couponId, productId).orElseThrow(() -> new JpaObjectRetrievalFailureException(new EntityNotFoundException("멤버쿠폰이 존재하지 않습니다")));
    }

    @Override
    public void deleteAllInBatch() {
        memberCouponJpaRepository.deleteAllInBatch();
    }

    @Override
    public MemberCoupon save(MemberCoupon memberCoupon) {
        return memberCouponJpaRepository.save(memberCoupon);
    }

    @Override
    public void saveAll(List<MemberCoupon> memberCoupons) {
        memberCouponJpaRepository.saveAll(memberCoupons);
    }

    @Override
    public List<MemberCoupon> findAll() {
        return memberCouponJpaRepository.findAll();
    }
}

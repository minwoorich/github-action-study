package com.objects.marketbridge.domains.member.infra.wishlist;

import com.objects.marketbridge.domains.member.domain.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishListJpaRepository extends JpaRepository<Wishlist,Long> {

    @Query("SELECT w FROM Wishlist w WHERE w.member.id = :memberId")
    List<Wishlist> findByMemberId(@Param("memberId") Long memberId);

    void deleteByMemberIdAndProductId(Long memberId, Long productId);
}
package com.objects.marketbridge.domains.order.infra.address;

import com.objects.marketbridge.domains.member.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressJpaRepository extends JpaRepository<Address, Long> {
    List<Address> findByMemberId(Long memberId);

    @Query("SELECT a FROM Address a WHERE a.member.id = :memberId AND a.isDefault = true")
    Optional<Address> findDefaultAddress(@Param("memberId") Long memberId);

    @Query("SELECT COUNT(a) FROM Address a WHERE a.member.id = :memberId")
    Long countAddress(@Param("memberId") Long memberId);

    @Query("DELETE FROM Address a WHERE a.id = :addressId ")
    void deleteById(@Param("addressId") Long addressId);
}

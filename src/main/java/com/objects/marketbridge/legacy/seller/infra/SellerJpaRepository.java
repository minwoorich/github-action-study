//package com.objects.marketbridge.seller.infra;
//
//import com.objects.marketbridge.seller.domain.Seller;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.Optional;
//
//public interface SellerJpaRepository extends JpaRepository<Seller, Long> {
//    @Query("select distinct s from Seller s join fetch s.sellerAccounts where s.id = :id")
//    Optional<Seller> findByIdWithSellerAccount(@Param("id") Long id);
//}

//package com.objects.marketbridge.seller.infra;
//
//import com.objects.marketbridge.seller.domain.SellerAccount;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface SellerAccountJpaRepository extends JpaRepository<SellerAccount, Long> {
//
//    @Query("select sa from SellerAccount sa WHERE sa.seller.id = :sellerId")
//    List<SellerAccount> findBySellerId(@Param("sellerId") Long sellerId);
//}

//package com.objects.marketbridge.seller.infra;
//
//import com.objects.marketbridge.seller.domain.SellerAccount;
//import com.objects.marketbridge.seller.service.port.SellerAccountRepository;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//@RequiredArgsConstructor
//public class SellerAccountRepoitoryImpl implements SellerAccountRepository {
//
//    private final SellerAccountJpaRepository sellerAccountJpaRepository;
//
//
//    @Override
//    public SellerAccount findById(Long id) {
//        return sellerAccountJpaRepository.findById(id).orElseThrow(EntityNotFoundException::new);
//    }
//
//    @Override
//    public List<SellerAccount> findBySellerId(Long sellerId) {
//        return sellerAccountJpaRepository.findBySellerId(sellerId);
//    }
//
//    @Override
//    public SellerAccount save(SellerAccount sellerAccount) {
//        return sellerAccountJpaRepository.save(sellerAccount);
//    }
//
//    @Override
//    public List<SellerAccount> saveAll(List<SellerAccount> sellerAccounts) {
//        return sellerAccountJpaRepository.saveAll(sellerAccounts);
//    }
//}

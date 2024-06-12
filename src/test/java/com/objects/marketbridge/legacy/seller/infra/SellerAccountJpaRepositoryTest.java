//package com.objects.marketbridge.seller.infra;
//
//import com.objects.marketbridge.seller.domain.Seller;
//import com.objects.marketbridge.seller.domain.SellerAccount;
//import com.objects.marketbridge.seller.service.port.SellerAccountRepository;
//import com.objects.marketbridge.seller.service.port.SellerRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.List;
//import java.util.Optional;
//
//@SpringBootTest
//@Slf4j
//@ActiveProfiles("test")
//class SellerAccountJpaRepositoryTest {
//
//    @Autowired SellerAccountJpaRepository sellerAccountJpaRepository;
//    @Autowired SellerRepository sellerRepository;
//
//    @BeforeEach
//    void init() {
//        Seller seller = Seller.builder()
//                .name("홍길동")
//                .build();
//        sellerRepository.save(seller);
//
//        SellerAccount account1 = SellerAccount.builder()
//                .balance(10000L)
//                .seller(seller)
//                .build();
//        SellerAccount account2 = SellerAccount.builder()
//                .balance(5000L)
//                .seller(seller)
//                .build();
//
//        sellerAccountJpaRepository.saveAll(List.of(account1, account2));
//    }
//
//    @Test
//    @DisplayName("Optional 타입의 SellerAccount 를 sellerId를 통해 조회할 수 있다")
//    void findBySellerId() {
//
//        // given
//        List<SellerAccount> sellerAccounts = sellerAccountJpaRepository.findBySellerId(1L);
//
//        //when
//        List<SellerAccount> results = sellerAccountJpaRepository.findBySellerId(1L);
//
//        // then
//        Assertions.assertThat(results.get(0).getSeller().getId()).isEqualTo(sellerAccounts.get(0).getSeller().getId());
//        Assertions.assertThat(results.get(1).getSeller().getId()).isEqualTo(sellerAccounts.get(1).getSeller().getId());
//    }
//}
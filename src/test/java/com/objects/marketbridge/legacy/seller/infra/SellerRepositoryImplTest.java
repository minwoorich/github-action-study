//package com.objects.marketbridge.seller.infra;
//
//import com.objects.marketbridge.seller.domain.Seller;
//import com.objects.marketbridge.seller.domain.SellerAccount;
//import com.objects.marketbridge.seller.service.port.SellerRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.*;
//
//@SpringBootTest
//@Slf4j
//@ActiveProfiles("test")
//class SellerRepositoryImplTest {
//
//    @Autowired SellerRepository sellerRepository;
//    @DisplayName("id를 통해 seller와 sellerAccount 모두 fetch join 해서 가져올수있다.")
//    @Test
//    void findByIdWithSellerAccount(){
//        //given
//        // 1. seller 생성
//        Seller seller = createSeller();
//
//        // 2. sellerAccount들 생성
//        List<SellerAccount> sellerAccounts = createSellerAccounts();
//
//        // 3. 연관관계 맺기 및 저장
//        seller.linkSellerAccounts(sellerAccounts.get(0));
//        seller.linkSellerAccounts(sellerAccounts.get(1));
//        sellerRepository.save(seller);
//
//        //when
//        Seller findSeller = sellerRepository.findByIdWithSellerAccount(seller.getId());
//
//        //then
//        assertThat(findSeller.getSellerAccounts()).hasSize(2);
//        assertThat(findSeller.getSellerAccounts().get(0).getBalance()).isEqualTo(10000L);
//        assertThat(findSeller.getSellerAccounts().get(1).getBalance()).isEqualTo(12000L);
//        assertThat(findSeller.getSellerAccounts().get(0).getId()).isEqualTo(sellerAccounts.get(0).getId());
//        assertThat(findSeller.getSellerAccounts().get(1).getId()).isEqualTo(sellerAccounts.get(1).getId());
//    }
//
//    private Seller createSeller() {
//        return Seller.builder()
//                .name("홍길동")
//                .build();
//    }
//    private List<SellerAccount> createSellerAccounts() {
//        SellerAccount sellerAccount1 = SellerAccount.builder()
//                .balance(10000L)
//                .incoming(0L)
//                .outgoing(0L)
//                .build();
//
//        SellerAccount sellerAccount2 = SellerAccount.builder()
//                .balance(12000L)
//                .incoming(2000L)
//                .outgoing(0L)
//                .build();
//        return List.of(sellerAccount1, sellerAccount2);
//    }
//}
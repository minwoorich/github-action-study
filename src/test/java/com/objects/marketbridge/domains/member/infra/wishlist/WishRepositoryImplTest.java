package com.objects.marketbridge.domains.member.infra.wishlist;

import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.domain.Wishlist;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.member.service.port.WishRepository;
import com.objects.marketbridge.domains.product.domain.Product;
import com.objects.marketbridge.domains.product.service.port.OptionRepository;
import com.objects.marketbridge.domains.product.service.port.ProdOptionRepository;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;


@Slf4j
@SpringBootTest
//@Transactional
@ActiveProfiles("test")
class WishRepositoryImplTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OptionRepository optionRepository;

    @Autowired
    ProdOptionRepository prodOptionRepository;

    @Autowired
    WishRepository wishRepository;

    @Autowired
    MemberRepository memberRepository;
    @Test
    void findByMemberId() throws Exception{
        //given
        Pageable pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));

        Member member = Member.builder()
                .email("test@naver.com").build();

        memberRepository.save(member);

        Product product1 = Product.builder()
                .productNo("1")
                .price(100L).build();

        Product product2 = Product.builder()
                .productNo("2")
                .price(200L).build();

        Product product3 = Product.builder()
                .productNo("3")
                .price(300L).build();

        Product product4 = Product.builder()
                .productNo("4")
                .price(400L).build();

        Product product5 = Product.builder()
                .productNo("5")
                .price(500L).build();


//        Option option1 = Option.builder()
//                .name("option1").build();
//
//        Option option2 = Option.builder()
//                .name("option2").build();
//
//        optionRepository.save(option1);
//        optionRepository.save(option2);
//
//        ProdOption prodOption1_1 = ProdOption.builder()
//                .option(option1).build();
//        ProdOption prodOption1_2 = ProdOption.builder()
//                .option(option2).build();
//
//
//        ProdOption prodOption2_1 = ProdOption.builder()
//                .option(option1).build();
//        ProdOption prodOption2_2 = ProdOption.builder()
//                .option(option2).build();
//
//
//        ProdOption prodOption3_1 = ProdOption.builder()
//                .option(option1).build();
//        ProdOption prodOption3_2 = ProdOption.builder()
//                .option(option2).build();
//
//
//        ProdOption prodOption4_1 = ProdOption.builder()
//                .option(option1).build();
//        ProdOption prodOption4_2 = ProdOption.builder()
//                .option(option2).build();
//
//
//        ProdOption prodOption5_1 = ProdOption.builder()
//                .option(option1).build();
//        ProdOption prodOption5_2 = ProdOption.builder()
//                .option(option2).build();
//
//
//        product1.addProdOptions(prodOption1_1);
//        product1.addProdOptions(prodOption1_2);
//
//        product2.addProdOptions(prodOption2_1);
//        product2.addProdOptions(prodOption2_2);
//
//        product3.addProdOptions(prodOption3_1);
//        product3.addProdOptions(prodOption3_2);
//
//        product4.addProdOptions(prodOption4_1);
//        product4.addProdOptions(prodOption4_2);
//
//        product5.addProdOptions(prodOption5_1);
//        product5.addProdOptions(prodOption5_2);

        productRepository.saveAll(List.of(product1,product2,product3,product4,product5));

        Wishlist wishlist1 = Wishlist.builder()
                .member(member)
                .product(product1).build();

        Wishlist wishlist2 = Wishlist.builder()
                .member(member)
                .product(product2).build();

        Wishlist wishlist3 = Wishlist.builder()
                .member(member)
                .product(product3).build();

        Wishlist wishlist4 = Wishlist.builder()
                .member(member)
                .product(product4).build();

        Wishlist wishlist5 = Wishlist.builder()
                .member(member)
                .product(product5).build();

        wishRepository.saveAndFlush(wishlist1);
        wishRepository.saveAndFlush(wishlist2);
        wishRepository.saveAndFlush(wishlist3);
        wishRepository.saveAndFlush(wishlist4);
        wishRepository.saveAndFlush(wishlist5);

        //when
        Slice<Wishlist> wishlists = wishRepository.findByMemberId(pageRequest,member.getId());

        log.info("prduct_id , {}",wishlists.getContent().get(0).getProduct().getId());

        //then
        Assertions.assertThat(wishlists.getSize()).isEqualTo(2);
        Assertions.assertThat(wishlists.getContent().get(0).getId()).isNotNull();
        Assertions.assertThat(wishlists.getContent().get(0).getProduct().getPrice()).isEqualTo(500L);
        Assertions.assertThat(wishlists.getContent().get(0).getMember().getEmail()).isEqualTo("test@naver.com");

        Assertions.assertThat(wishlists.isFirst()).isTrue();
        Assertions.assertThat(wishlists.getNumberOfElements()).isEqualTo(2);
    }


    @Test
    @DisplayName("wishlist의 해당 product의 존재여부 확인")
    void countByProductIdAndMemberId() throws Exception{
        //given
        Member member = Member.builder()
                .email("test@naver.com").build();

        memberRepository.save(member);

        Product product1 = Product.builder()
                .productNo("1")
                .price(100L).build();

        Product product2 = Product.builder()
                .productNo("2")
                .price(1000L).build();

        productRepository.saveAll(List.of(product1,product2));

        Wishlist wishlist1 = Wishlist.builder()
                .member(member)
                .product(product1).build();

        wishRepository.saveAll(List.of(wishlist1));
        //when
        Long result1 = wishRepository.countByProductIdAndMemberId(member.getId(), product1.getId());
        Long result2 = wishRepository.countByProductIdAndMemberId(member.getId(), product2.getId());


        //then
        Assertions.assertThat(result1).isEqualTo(1L);
        Assertions.assertThat(result2).isEqualTo(0L);
     }

}
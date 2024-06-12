package com.objects.marketbridge.domains.member.service;

import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.domain.Wishlist;
import com.objects.marketbridge.domains.member.dto.WishlistRequest;
import com.objects.marketbridge.domains.member.service.MemberService;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.member.service.port.WishRepository;
import com.objects.marketbridge.domains.product.domain.Product;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MemberServiceTestWithSpringBoot {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    WishRepository wishRepository;

    @Autowired
    MemberService memberService;


    @Test
    @DisplayName("wishlist에 추가")
    public void addWishlist(){
        //given
        Product product= Product.builder()
                .stock(1L)
                .name("Product Name")
                .thumbImg("image/static")
                .price(1000L)
                .isOwn(false)
                .build();

        productRepository.save(product);


        Member member = Member.builder()
                .email("WishTest@naver.com").build();

        memberRepository.save(member);

        WishlistRequest request = WishlistRequest.builder()
                .productId(product.getId())
                .build();
        //when
        memberService.addWish(member.getId(),request);
        Wishlist findWishlist = wishRepository.findByMemberId(member.getId()).get(0);
        //then
        Assertions.assertThat(findWishlist.getProduct().getName()).isEqualTo("Product Name");
        Assertions.assertThat(findWishlist.getMember().getEmail()).isEqualTo("WishTest@naver.com");

    }

    @Test
    @DisplayName("wishlist 삭제")
    void deleteWishlist() throws Exception{
        //given
        Product product1= Product.builder()
                .stock(1L)
                .name("product1")
                .thumbImg("image/static")
                .price(1000L)
                .isOwn(false)
                .build();

        Product product2= Product.builder()
                .stock(2L)
                .name("product2")
                .thumbImg("image2/static")
                .price(2000L)
                .isOwn(false)
                .build();

        productRepository.save(product1);
        productRepository.save(product2);

        Member member = Member.builder()
                .email("WishTest@naver.com").build();

        memberRepository.save(member);

        WishlistRequest request1 = WishlistRequest.builder()
                .productId(product1.getId())
                .build();

        WishlistRequest request2 = WishlistRequest.builder()
                .productId(product2.getId())
                .build();

        memberService.addWish(member.getId(),request1);
        memberService.addWish(member.getId(),request2);
        //when

        memberService.deleteWishlist(member.getId(),request1);

        Wishlist findWishlist = wishRepository.findByMemberId(member.getId()).get(0);

        //then
        Assertions.assertThat(findWishlist.getProduct().getId()).isEqualTo(request2.getProductId());
        Assertions.assertThat(findWishlist.getProduct().getName()).isEqualTo("product2");
        Assertions.assertThat(findWishlist.getMember().getEmail()).isEqualTo("WishTest@naver.com");


    }
}
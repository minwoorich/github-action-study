package com.objects.marketbridge.domains.cart.service;


import com.objects.marketbridge.common.responseobj.SliceResponse;
import com.objects.marketbridge.domains.cart.domain.Cart;
import com.objects.marketbridge.domains.cart.service.dto.GetCartDto;
import com.objects.marketbridge.domains.cart.service.port.CartCommandRepository;
import com.objects.marketbridge.domains.cart.service.port.CartDtoRepository;
import com.objects.marketbridge.domains.cart.service.port.CartQueryRepository;
import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.coupon.service.port.CouponRepository;
import com.objects.marketbridge.domains.coupon.service.port.MemberCouponRepository;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.product.domain.Option;
import com.objects.marketbridge.domains.product.domain.ProdOption;
import com.objects.marketbridge.domains.product.domain.Product;
import com.objects.marketbridge.domains.product.service.port.OptionRepository;
import com.objects.marketbridge.domains.product.service.port.ProdOptionRepository;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
@Transactional
class GetCartListServiceTest {

    @Autowired CartQueryRepository cartQueryRepository;
    @Autowired CartDtoRepository cartDtoRepository;
    @Autowired CartCommandRepository cartCommandRepository;
    @Autowired ProductRepository productRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired GetCartListService getCartListService;
    @Autowired OptionRepository optionRepository;
    @Autowired ProdOptionRepository prodOptionRepository;
    @Autowired CouponRepository couponRepository;
    @Autowired MemberCouponRepository memberCouponRepository;
    @Autowired EntityManager em;

    @BeforeEach
    void init() {
        Member member = Member.builder().email("test@email.com").build();
        memberRepository.save(member);

        Option option1 = Option.builder().name("옵션1").build();
        Option option2 = Option.builder().name("옵션2").build();

        ProdOption prodOption1_1 = ProdOption.builder().build();
        ProdOption prodOption1_2 = ProdOption.builder().build();
        ProdOption prodOption2_1 = ProdOption.builder().build();
        ProdOption prodOption2_2 = ProdOption.builder().build();
        ProdOption prodOption3_1 = ProdOption.builder().build();
        ProdOption prodOption3_2 = ProdOption.builder().build();
        ProdOption prodOption4_1 = ProdOption.builder().build();
        ProdOption prodOption4_2 = ProdOption.builder().build();
        ProdOption prodOption5_1 = ProdOption.builder().build();
        ProdOption prodOption5_2 = ProdOption.builder().build();
        ProdOption prodOption6_1 = ProdOption.builder().build();
        ProdOption prodOption6_2 = ProdOption.builder().build();

        option1.addProdOptions(prodOption1_1);
        option1.addProdOptions(prodOption2_1);
        option1.addProdOptions(prodOption3_1);
        option1.addProdOptions(prodOption4_1);
        option1.addProdOptions(prodOption5_1);
        option1.addProdOptions(prodOption6_1);
        option2.addProdOptions(prodOption1_2);
        option2.addProdOptions(prodOption2_2);
        option2.addProdOptions(prodOption3_2);
        option2.addProdOptions(prodOption4_2);
        option2.addProdOptions(prodOption5_2);
        option2.addProdOptions(prodOption6_2);

        optionRepository.saveAll(List.of(option1, option2));

        Product product1 = Product.builder().stock(5L).productNo("111111 - 111111").build();
        Product product2 = Product.builder().stock(5L).productNo("222222 - 222222").build();
        Product product3 = Product.builder().stock(5L).productNo("333333 - 333333").build();
        Product product4 = Product.builder().stock(5L).productNo("444444 - 444444").build();
        Product product5 = Product.builder().stock(5L).productNo("555555 - 555555").build();
        Product product6 = Product.builder().stock(5L).productNo("666666 - 666666").build();

        product1.addProdOptions(prodOption1_1);
        product1.addProdOptions(prodOption1_2);

        product2.addProdOptions(prodOption2_1);
        product2.addProdOptions(prodOption2_2);

        product3.addProdOptions(prodOption3_1);
        product3.addProdOptions(prodOption3_2);

        product4.addProdOptions(prodOption4_1);
        product4.addProdOptions(prodOption4_2);

        product5.addProdOptions(prodOption5_1);
        product5.addProdOptions(prodOption5_2);

        product6.addProdOptions(prodOption6_1);
        product6.addProdOptions(prodOption6_2);

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        productRepository.save(product4);
        productRepository.save(product5);
        productRepository.save(product6);

        Cart cart1 = Cart.create(member, product1, false, 1L);
        Cart cart2 = Cart.create(member, product2, false, 1L);
        Cart cart3 = Cart.create(member, product3, false, 1L);
        Cart cart4 = Cart.create(member, product4, false, 1L);
        Cart cart5 = Cart.create(member, product5, false, 1L);
        Cart cart6 = Cart.create(member, product6, false, 1L);


        cartCommandRepository.saveAndFlush(cart1);
        cartCommandRepository.saveAndFlush(cart2);
        cartCommandRepository.saveAndFlush(cart3);
        cartCommandRepository.saveAndFlush(cart4);
        cartCommandRepository.saveAndFlush(cart5);
        cartCommandRepository.saveAndFlush(cart6);

    }

    @AfterEach
    void clear() {
        cartCommandRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        prodOptionRepository.deleteAllInBatch();
        optionRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        couponRepository.deleteAllInBatch();
    }


    @DisplayName("SliceResponse 로 변환된다")
    @Test
    void get_SliceResponse(){
        //given
        Member member = memberRepository.findByEmail("test@email.com");
        int pageNumber = 0;
        int pageSize = 2;
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        //when
        SliceResponse<GetCartDto> sliceResponse = getCartListService.get(pageRequest, member.getId());

        //then
        assertThat(sliceResponse).isInstanceOf(SliceResponse.class);
        assertThat(sliceResponse.getSize()).isEqualTo(pageSize);
        assertThat(sliceResponse.getSort().getDirection()).isEqualTo(Sort.Direction.DESC.toString());
        assertThat(sliceResponse.getSort().getOrderProperty()).isEqualTo("createdAt");
        assertThat(sliceResponse.getCurrentPage()).isEqualTo(0);
        assertThat(sliceResponse.isFirst()).isTrue();
        assertThat(sliceResponse.isLast()).isFalse();
        assertThat(sliceResponse.getContent()).hasSize(pageSize);
    }

    @DisplayName("장바구니를 조회할 수 있다_쿠폰제외")
    @Test
    void get_withoutCoupon(){
        //given
        Member member = memberRepository.findByEmail("test@email.com");
        int pageNumber = 0;
        int pageSize = 2;
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        //when
        SliceResponse<GetCartDto> sliceResponse = getCartListService.get(pageRequest, member.getId());

        //then
        // response.getCartItems().getContent()의 각 요소가 GetCartDto 클래스의 인스턴스인지 확인
        assertThat(sliceResponse.getContent())
                .allSatisfy(cartItem -> assertThat(cartItem).isInstanceOf(GetCartDto.class));

        // 각각의 CartInfo 객체가 2개의 옵션을 가지고 있으며 각 옵션 이름은 '옵션1', '옵션2' 여야 한다.
        assertThat(sliceResponse.getContent())
                .extracting(GetCartDto::getOptionNames)
                .allMatch(optionNames -> optionNames.size() == 2)
                .allSatisfy(optionNames -> {
                    assertThat(optionNames).containsExactly("옵션1", "옵션2");
                });
    }

    @DisplayName("장바구니에 담긴 총 물건 수를 조회 할 수 있다")
    @Test
    void countAll(){
        //given
        Long memberId = memberRepository.findByEmail("test@email.com").getId();

        //when
        Long countResult = getCartListService.countAll(memberId);

        //then
        assertThat(countResult).isEqualTo(6);
    }
}
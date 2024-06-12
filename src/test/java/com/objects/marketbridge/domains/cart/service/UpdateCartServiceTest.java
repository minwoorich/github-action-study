package com.objects.marketbridge.domains.cart.service;

import com.objects.marketbridge.domains.cart.domain.Cart;
import com.objects.marketbridge.domains.cart.service.UpdateCartService;
import com.objects.marketbridge.domains.cart.service.dto.UpdateCartDto;
import com.objects.marketbridge.domains.cart.service.port.CartCommandRepository;
import com.objects.marketbridge.domains.cart.service.port.CartQueryRepository;
import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
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

import static org.assertj.core.api.Assertions.catchThrowable;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
class UpdateCartServiceTest {

    @Autowired
    UpdateCartService updateCartService;
    @Autowired ProductRepository productRepository;
    @Autowired CartQueryRepository cartQueryRepository;
    @Autowired CartCommandRepository cartCommandRepository;
    @Autowired MemberRepository memberRepository;

    @DisplayName("장바구니 수량을 수정 할 수 있다")
    @Test
    void update1(){
        //given
        Product product = Product.builder().productNo("productNo1").build();
        productRepository.save(product);

        Member member = Member.builder().email("test@email.com").build();
        memberRepository.save(member);

        Cart cart = Cart.builder().member(member).product(product).build();
        cartCommandRepository.save(cart);

        UpdateCartDto updateDto = UpdateCartDto.builder().cartId(cart.getId()).quantity(3L).build();

        //when
        updateCartService.update(updateDto);

        //then
        Assertions.assertThat(cart.getQuantity()).isEqualTo(updateDto.getQuantity());
    }

    @DisplayName("장바구니 수량은 1 미만, 100 초과의 값을 가질 수 없다")
    @Test
    void update2(){
        //given
        Product product = Product.builder().productNo("productNo1").build();
        productRepository.save(product);

        Member member = Member.builder().email("test@email.com").build();
        memberRepository.save(member);

        Cart cart = Cart.builder().member(member).product(product).build();
        cartCommandRepository.save(cart);

        UpdateCartDto updateDto1 = UpdateCartDto.builder().cartId(cart.getId()).quantity(1L).build();
        UpdateCartDto updateDto2 = UpdateCartDto.builder().cartId(cart.getId()).quantity(100L).build();
        UpdateCartDto updateDto3 = UpdateCartDto.builder().cartId(cart.getId()).quantity(0L).build();
        UpdateCartDto updateDto4 = UpdateCartDto.builder().cartId(cart.getId()).quantity(101L).build();

        //when
        updateCartService.update(updateDto1);
        updateCartService.update(updateDto2);
        Throwable thrown1 = catchThrowable(() -> updateCartService.update(updateDto3));
        Throwable thrown2 = catchThrowable(() -> updateCartService.update(updateDto4));

        //then
        Assertions.assertThatCode(() -> updateCartService.update(updateDto1))
                .doesNotThrowAnyException();
        Assertions.assertThatCode(() -> updateCartService.update(updateDto2))
                .doesNotThrowAnyException();
        Assertions.assertThat(thrown1).isInstanceOf(CustomLogicException.class);
        Assertions.assertThat(thrown2).isInstanceOf(CustomLogicException.class);
    }
}
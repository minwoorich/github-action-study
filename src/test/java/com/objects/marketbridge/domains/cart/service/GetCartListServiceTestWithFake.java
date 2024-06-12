package com.objects.marketbridge.domains.cart.service;


import com.objects.marketbridge.domains.cart.domain.Cart;
import com.objects.marketbridge.domains.cart.mock.BaseFakeCartRepository;
import com.objects.marketbridge.domains.cart.mock.FakeCartCommandRepository;
import com.objects.marketbridge.domains.cart.mock.FakeCartQueryRepository;
import com.objects.marketbridge.domains.cart.service.port.CartCommandRepository;
import com.objects.marketbridge.domains.cart.service.port.CartQueryRepository;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.product.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class GetCartListServiceTestWithFake {

    CartQueryRepository cartQueryRepository = new FakeCartQueryRepository();
    CartCommandRepository cartCommandRepository = new FakeCartCommandRepository();

    @AfterEach
    void afterEach() {
        BaseFakeCartRepository.getInstance().clear();
    }

    @DisplayName("장바구니에 담긴 총 물건 수를 조회 할 수 있다")
    @Test
    void countAll() {
        // given
        Member member = Member.builder().id(1L).name("홍길동").email("test@email.com").build();
        Product product1 = Product.builder().name("가방").price(1000L).build();
        Product product2 = Product.builder().name("신발").price(1000L).build();
        Product product3 = Product.builder().name("PC").price(2000L).build();

        Cart cart1 = Cart.builder().product(product1).member(member).isSubs(false).quantity(2L).build();
        Cart cart2 = Cart.builder().product(product2).member(member).isSubs(false).quantity(1L).build();
        Cart cart3 = Cart.builder().product(product3).member(member).isSubs(false).quantity(4L).build();
        cartCommandRepository.saveAll(List.of(cart1, cart2, cart3));

        // when
        GetCartListService service = GetCartListService.builder().cartQueryRepository(cartQueryRepository).build();
        Long result = service.countAll(1L);

        //then
        Assertions.assertThat(result).isEqualTo(3);
    }
}

package com.objects.marketbridge.domains.cart.domain;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.exception.exceptions.ErrorCode;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.product.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ActiveProfiles("test")
@Slf4j
class CartTest {
    @DisplayName("장바구니 수량을 수정할 수 있다")
    @Test
    void updateQuantity(){
        //given
        Cart cart1 = Cart.builder().quantity(1L).build();
        Cart cart2 = Cart.builder().quantity(1L).build();
        Cart cart3 = Cart.builder().quantity(1L).build();

        Long quantity1 = 10L;
        Long quantity2 = 1L;
        Long quantity3 = 100L;

        //when
        Cart updatedCart1 = cart1.updateQuantity(quantity1);
        Cart updatedCart2 = cart2.updateQuantity(quantity2);
        Cart updatedCart3 = cart3.updateQuantity(quantity3);

        //then
        Assertions.assertThat(updatedCart1.getQuantity()).isEqualTo(10L);
        Assertions.assertThat(updatedCart2.getQuantity()).isEqualTo(1L);
        Assertions.assertThat(updatedCart3.getQuantity()).isEqualTo(100L);
    }

    @DisplayName("장바구니 수량은 1미만 100초과 할 수 없다")
    @Test
    void updateQuantity_error(){
        //given
        Cart cart1 = Cart.builder().quantity(1L).build();
        Cart cart2 = Cart.builder().quantity(1L).build();

        Long quantity1 = 0L;
        Long quantity2 = 1020L;

        //when
        Throwable thrown1 = catchThrowable(() -> cart1.updateQuantity(quantity1));
        Throwable thrown2 = catchThrowable(() -> cart2.updateQuantity(quantity2));

        //then
        assertThat(thrown1).isInstanceOf(CustomLogicException.class).hasMessage("장바구니 수량은 0 이상 100 이하 입니다");
        assertThat(thrown2).isInstanceOf(CustomLogicException.class).hasMessage("장바구니 수량은 0 이상 100 이하 입니다");
    }

    @DisplayName("장바구니를 생성 할 수 있다.")
    @Test
    void create() {
        // given
        Member member = Member.builder().name("임꺽정").build();
        Product product = Product.builder().productNo("productNo1").stock(100L).build();

        // when
        Cart cart = Cart.create(member, product, false, 1L);

        //then
        Assertions.assertThat(cart.getQuantity()).isEqualTo(1L);
        Assertions.assertThat(cart.getMember().getName()).isEqualTo("임꺽정");
        Assertions.assertThat(cart.getProduct().getProductNo()).isEqualTo("productNo1");
        Assertions.assertThat(cart.getIsSubs()).isFalse();

    }

    @DisplayName("재고보다 많은 수량을 담으려고 할 경우 에러가 발생한다.")
    @Test
    void create_error_with_stock() {
        // given
        Member member = Member.builder().name("임꺽정").build();
        Product product = Product.builder().productNo("productNo1").stock(1L).build();

        // when
        Throwable thrown = catchThrowable(() -> Cart.create(member, product, false, 10L));

        //then
        assertThat(thrown).isInstanceOf(CustomLogicException.class).hasMessage(ErrorCode.OUT_OF_STOCK.getMessage());
    }

}
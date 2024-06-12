package com.objects.marketbridge.domains.cart.infra;

import com.objects.marketbridge.domains.cart.domain.Cart;
import com.objects.marketbridge.domains.cart.mock.BaseFakeCartRepository;
import com.objects.marketbridge.domains.cart.mock.FakeCartCommandRepository;
import com.objects.marketbridge.domains.cart.mock.FakeCartQueryRepository;
import com.objects.marketbridge.domains.cart.service.port.CartCommandRepository;
import com.objects.marketbridge.domains.cart.service.port.CartQueryRepository;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.mock.FakeMemberRepository;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.product.domain.Product;
import com.objects.marketbridge.domains.product.mock.FakeProductRepository;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class FakeCartQueryRepositoryTest {

    CartQueryRepository cartQueryRepository = new FakeCartQueryRepository();
    MemberRepository memberRepository = new FakeMemberRepository();
    ProductRepository productRepository = new FakeProductRepository();
    CartCommandRepository cartCommandRepository = new FakeCartCommandRepository();

    @DisplayName("Id 로 장바구니를 조회할 수 있다.")
    @Test
    void findById() {
        // given
        Cart cart1 = Cart.builder().quantity(1L).build();
        Cart cart2 = Cart.builder().quantity(2L).build();
        Cart cart3 = Cart.builder().quantity(3L).build();
        cartCommandRepository.saveAll(List.of(cart1, cart2, cart3));

        // when
        Cart findCart1 = cartQueryRepository.findById(1L);
        Cart findCart2 = cartQueryRepository.findById(2L);
        Cart findCart3 = cartQueryRepository.findById(3L);

        //then
        assertThat(findCart1)
                .extracting(Cart::getId, Cart::getQuantity)
                .containsExactly(1L, 1L);
        assertThat(findCart2)
                .extracting(Cart::getId, Cart::getQuantity)
                .containsExactly(2L, 2L);
        assertThat(findCart3)
                .extracting(Cart::getId, Cart::getQuantity)
                .containsExactly(3L, 3L);
    }

    @DisplayName("모든 회원들의 전체 장바구니를 조회할 수 있다")
    @Test
    void findAll() {
        // given
        Cart cart1 = Cart.builder().quantity(1L).build();
        Cart cart2 = Cart.builder().quantity(2L).build();
        Cart cart3 = Cart.builder().quantity(3L).build();
        cartCommandRepository.saveAll(List.of(cart1, cart2, cart3));

        // when
        List<Cart> carts = cartQueryRepository.findAll();

        //then
        assertThat(carts)
                .hasSize(3)
                .extracting(Cart::getId, Cart::getQuantity)
                .containsExactly(
                        tuple(1L, 1L),
                        tuple(2L, 2L),
                        tuple(3L, 3L)
                );
    }

    @DisplayName("회원과 상품아이디를 통해 장바구니를 조회할 수 있다")
    @Test
    void findByProductIdAndMemberId() {
        // given
        Long productId = 2L;
        Long memberId = 1L;
        Member member = Member.builder().name("임꺽정").build();
        memberRepository.save(member);

        Product product1 = productRepository.save(Product.builder().name("가방").productNo("productNo1").build());
        Product product2 = productRepository.save(Product.builder().name("신발").productNo("productNo2").build());

        Cart cart1 = Cart.builder().quantity(1L).member(member).product(product1).build();
        Cart cart2 = Cart.builder().quantity(2L).member(member).product(product2).build();
        cartCommandRepository.saveAll(List.of(cart1, cart2));

        // when
        Optional<Cart> result = cartQueryRepository.findByProductIdAndMemberId(productId, memberId);

        //then
        assertThat(result.get()).extracting(c -> c.getProduct().getId(), c -> c.getMember().getId())
                .containsExactly(productId, memberId);
    }

    @DisplayName("회원이 담은 전체 장바구니 항목 수를 조회할 수 있다")
    @Test
    void countByMemberId() {
        // given
        Cart cart1 = Cart.builder().quantity(1L).build();
        Cart cart2 = Cart.builder().quantity(2L).build();
        Cart cart3 = Cart.builder().quantity(3L).build();
        cartCommandRepository.saveAll(List.of(cart1, cart2, cart3));

        // when
        List<Cart> carts = cartQueryRepository.findAll();

        //then
        assertThat(carts).hasSize(3);
    }

    @AfterEach
    void afterEach() {
        BaseFakeCartRepository.getInstance().clear();
        memberRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
    }
}

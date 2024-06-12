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

import static org.assertj.core.api.Assertions.assertThat;

public class FakeCartCommandRepositoryTest {

    ProductRepository productRepository = new FakeProductRepository();
    MemberRepository memberRepository = new FakeMemberRepository();
    CartCommandRepository cartCommandRepository = new FakeCartCommandRepository();
    CartQueryRepository cartQueryRepository = new FakeCartQueryRepository();

    @AfterEach
    void clear(){
        BaseFakeCartRepository.getInstance().clear();
        memberRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
    }

    @DisplayName("로그인한 회원의 장바구니에서 특정 상품들에 대한 장바구니들을 삭제할 수 있다.")
    @Test
    void deleteAllByProductIdsAndMemberIdInBatch(){
        //given
        Member member = memberRepository.save(Member.builder().name("홍길동").build());

        Product product1 = productRepository.save(Product.builder().name("가방").build());
        Product product2 = productRepository.save(Product.builder().name("책").build());
        Product product3 = productRepository.save(Product.builder().name("신발").build());

        cartCommandRepository.save(Cart.builder().member(member).product(product1).build());
        cartCommandRepository.save(Cart.builder().member(member).product(product2).build());
        cartCommandRepository.save(Cart.builder().member(member).product(product3).build());

        //when
        List<Long> deletedProductIds = List.of(product1.getId(), product2.getId());
        cartCommandRepository.deleteAllByProductIdsAndMemberIdInBatch(deletedProductIds, member.getId());

        //then
        List<Cart> carts = cartQueryRepository.findAll();
        assertThat(carts).hasSize(1);
        assertThat(carts)
                .extracting(Cart::getProduct)
                .extracting(Product::getName)
                .contains("신발");
    }
}

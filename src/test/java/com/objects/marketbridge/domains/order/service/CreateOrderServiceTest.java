package com.objects.marketbridge.domains.order.service;

import com.objects.marketbridge.domains.cart.domain.Cart;
import com.objects.marketbridge.domains.cart.service.port.CartCommandRepository;
import com.objects.marketbridge.domains.cart.service.port.CartQueryRepository;
import com.objects.marketbridge.domains.member.domain.Address;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.order.service.dto.CreateOrderDto;
import com.objects.marketbridge.domains.order.service.port.AddressRepository;
import com.objects.marketbridge.domains.product.domain.Product;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
@Transactional
public class CreateOrderServiceTest {

    @Autowired MemberRepository memberRepository;
    @Autowired AddressRepository addressRepository;
    @Autowired ProductRepository productRepository;
    @Autowired CartCommandRepository cartCommandRepository;
    @Autowired CartQueryRepository cartQueryRepository;
    @Autowired CreateOrderService createOrderService;

    @DisplayName("주문 완료시 장바구니 삭제")
    @Test
    void create_remove_cart_item() {
        //given
        Member member = memberRepository.save(Member.builder().name("홍길동").build());
        Address address = addressRepository.save(Address.builder().build());
        member.addAddress(address);

        Product product1 = productRepository.save(Product.builder()
                .id(1L)
                .stock(999L)
                .productNo("111111 - 111111")
                .name("가방")
                .price(20000L)
                .build());

        Product product2 = productRepository.save(Product.builder()
                .id(2L)
                .stock(999L)
                .productNo("111111 - 111111")
                .name("책")
                .price(20000L)
                .build());

        Product product3 = productRepository.save(Product.builder()
                .id(3L)
                .stock(999L)
                .productNo("111111 - 111111")
                .name("신발")
                .price(20000L)
                .build());

        cartCommandRepository.save(Cart.builder().member(member).product(product1).build());
        cartCommandRepository.save(Cart.builder().member(member).product(product2).build());
        cartCommandRepository.save(Cart.builder().member(member).product(product3).build());

        CreateOrderDto.ProductDto productDto1 = CreateOrderDto.ProductDto.builder()
                .productId(product1.getId())
                .price(20000L)
                .quantity(1L)
                .hasCouponUsed(false)
                .build();
        CreateOrderDto.ProductDto productDto2 = CreateOrderDto.ProductDto.builder()
                .productId(product2.getId())
                .price(20000L)
                .quantity(1L)
                .hasCouponUsed(false)
                .build();
        CreateOrderDto.ProductDto productDto3 = CreateOrderDto.ProductDto.builder()
                .productId(product3.getId())
                .price(20000L)
                .quantity(1L)
                .hasCouponUsed(false)
                .build();

        CreateOrderDto createOrderDto = CreateOrderDto.builder()
                .tid("tid")
                .memberId(member.getId())
                .addressId(1L)
                .orderName("가방")
                .orderNo("1111-2222-3333-4444")
                .totalOrderPrice(60000L)
                .realOrderPrice(60000L)
                .totalDiscountPrice(0L)
                .productValues(List.of(productDto1, productDto2, productDto3))
                .build();

        //when
        createOrderService.create(createOrderDto);

        //then
        List<Cart> carts = cartQueryRepository.findAll();
        assertThat(carts).isEmpty();
    }
}

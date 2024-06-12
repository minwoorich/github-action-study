package com.objects.marketbridge.domains.order.service.port;

import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.order.domain.Order;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class OrderCommandRepositoryTest {

    @Autowired
    OrderCommandRepository orderCommandRepository;

    @Autowired
    OrderQueryRepository orderQueryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("주문ID로 주문을 조회한다.")
    public void findByOrderId() {
        // given
        Order order = Order.builder().build();
        Order savedOrder = orderCommandRepository.save(order);

        // when
        Optional<Order> foundOrder = orderQueryRepository.findById(savedOrder.getId());

        // then
        assertTrue(foundOrder.isPresent());
        assertThat(savedOrder.getId()).isEqualTo(foundOrder.get().getId());
    }

    @Test
    @DisplayName("저장된 주문이 없다면 주문을 조회 할 수 없다.")
    public void findByOrderIdNoSearch() {
        // given
        Long NoId = 1L;

        // when
        Optional<Order> empty = orderQueryRepository.findById(NoId);

        // then
        assertFalse(empty.isPresent());
        assertThat(empty).isEmpty();
    }

    @Test
    @DisplayName("주문을 저장한다.")
    public void save() {
        // given
        Order order = Order.builder().build();

        // when
        Order savedOrder = orderCommandRepository.save(order);

        // then
        assertNotNull(savedOrder.getId());

        Optional<Order> foundOrder = orderQueryRepository.findById(savedOrder.getId());
        assertTrue(foundOrder.isPresent());
        assertThat(savedOrder.getId()).isEqualTo(foundOrder.get().getId());
    }

}
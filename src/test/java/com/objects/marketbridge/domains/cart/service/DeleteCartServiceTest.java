package com.objects.marketbridge.domains.cart.service;

import com.objects.marketbridge.domains.cart.domain.Cart;
import com.objects.marketbridge.domains.cart.service.DeleteCartService;
import com.objects.marketbridge.domains.cart.service.port.CartCommandRepository;
import com.objects.marketbridge.domains.cart.service.port.CartQueryRepository;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
class DeleteCartServiceTest {

    @Autowired
    DeleteCartService deleteCartService ;
    @Autowired
    ProductRepository productRepository;
    @Autowired CartQueryRepository cartQueryRepository;
    @Autowired CartCommandRepository cartCommandRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @AfterEach
    void clear() {
        productRepository.deleteAllInBatch();
        cartCommandRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("선택한 장바구니 항목들을 삭제할 수 있다")
    @Test
    void delete1(){
        //given
        Cart cart1 = Cart.builder().build();
        Cart cart2 = Cart.builder().build();
        Cart cart3 = Cart.builder().build();
        Cart cart4 = Cart.builder().build();
        cartCommandRepository.save(cart1);
        cartCommandRepository.save(cart2);
        cartCommandRepository.save(cart3);
        cartCommandRepository.save(cart4);

        List<Long> selected = List.of(cart1.getId());

        //when
        deleteCartService.delete(selected);
        em.flush();
        em.clear();


        //then
        assertThat(cartQueryRepository.findAll()).hasSize(3);
    }
    @DisplayName("삭제한 장바구니를 조회할 경우 에러가 발생한다")
    @Test
    void delete2(){
        //given
        Cart cart1 = Cart.builder().build();
        Cart cart2 = Cart.builder().build();
        Cart cart3 = Cart.builder().build();
        Cart cart4 = Cart.builder().build();
        cartCommandRepository.save(cart1);
        cartCommandRepository.save(cart2);
        cartCommandRepository.save(cart3);
        cartCommandRepository.save(cart4);

        List<Long> selected = List.of(cart1.getId());

        //when
        deleteCartService.delete(selected);
        em.flush();
        em.clear();

        Throwable thrown = catchThrowable(() -> cartQueryRepository.findById(selected.get(0)));

        //then
        Assertions.assertThat(thrown).isInstanceOf(JpaObjectRetrievalFailureException.class);
    }

}
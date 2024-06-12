package com.objects.marketbridge.domains.cart.infra;

import com.objects.marketbridge.domains.cart.domain.Cart;
import com.objects.marketbridge.domains.cart.service.port.CartCommandRepository;
import com.objects.marketbridge.domains.cart.service.port.CartQueryRepository;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.ThrowableAssert.catchThrowable;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
class CartCommandRepositoryImplTest {
    @Autowired CartQueryRepository cartQueryRepository;
    @Autowired CartCommandRepository cartCommandRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @DisplayName("선택된 장바구니 항목들을 삭제 할 수 있다")
    @Test
    void deleteAllByIdInBatch(){
        //given
        Cart cart1 = Cart.builder().build();
        Cart cart2 = Cart.builder().build();
        Cart cart3 = Cart.builder().build();
        Cart cart4 = Cart.builder().build();
        Cart cart5 = Cart.builder().build();
        cartCommandRepository.saveAll(List.of(cart1, cart2, cart3, cart4, cart5));

        List<Long> selected = List.of(cart1.getId(), cart2.getId());

        //when
        cartCommandRepository.deleteAllByIdInBatch(selected);
        em.clear();
        em.flush();

        Throwable thrown1 = catchThrowable(() -> cartQueryRepository.findById(cart1.getId()));
        Throwable thrown2 = catchThrowable(() -> cartQueryRepository.findById(cart2.getId()));

        //then
        Assertions.assertThat(thrown1).isInstanceOf(JpaObjectRetrievalFailureException.class);
        Assertions.assertThat(thrown2).isInstanceOf(JpaObjectRetrievalFailureException.class);
    }
}
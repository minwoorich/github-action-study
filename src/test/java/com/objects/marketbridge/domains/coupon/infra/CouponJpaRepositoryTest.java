package com.objects.marketbridge.domains.coupon.infra;

import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.coupon.service.port.CouponRepository;
import com.objects.marketbridge.domains.coupon.service.port.MemberCouponRepository;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.product.domain.Product;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
@ActiveProfiles("test")
class CouponJpaRepositoryTest {

    @Autowired ProductRepository productRepository;
    @Autowired CouponRepository couponRepository;
    @Autowired MemberCouponRepository memberCouponRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired CouponJpaRepository couponJpaRepository;

    @DisplayName("상품그룹에 등록된 모든 쿠폰들을 조회할 수 있다.")
    @Test
    void findByProductGroupId(){
        //given
        String productNo = "111111 - 111111";
        Product product = Product.builder().productNo(productNo).build();
        productRepository.save(product);

        Coupon coupon1 = Coupon.builder().productGroupId(111111L).price(1000L).count(100L).build();
        Coupon coupon2 = Coupon.builder().productGroupId(111111L).price(2000L).count(100L).build();
        Coupon coupon3 = Coupon.builder().productGroupId(111111L).price(3000L).count(100L).build();
        couponRepository.saveAll(List.of(coupon1, coupon2, coupon3));

        //when
        List<Coupon> coupons = couponRepository.findByProductGroupId(111111L);

        //then
        assertThat(coupons).hasSize(3);
    }

    @DisplayName("쿠폰을 조회할 때 MemberCoupons 들도 같이 fetch join 해서 가져온다. ")
    @Test
    void findByProductGroupIdWithMemberCoupons(){
        //given
        Member member = Member.builder().name("홍길동").build();
        memberRepository.save(member);

        Coupon coupon1 = Coupon.builder().productGroupId(111111L).price(1000L).count(999L).build();
        Coupon coupon2 = Coupon.builder().productGroupId(111111L).price(2000L).count(999L).build();
        Coupon coupon3 = Coupon.builder().productGroupId(111111L).price(3000L).count(999L).build();

        MemberCoupon memberCoupon1 = MemberCoupon.builder().member(member).build();
        MemberCoupon memberCoupon2 = MemberCoupon.builder().member(member).build();
        MemberCoupon memberCoupon3 = MemberCoupon.builder().member(member).build();

        coupon1.addMemberCoupon(memberCoupon1);
        coupon2.addMemberCoupon(memberCoupon2);
        coupon3.addMemberCoupon(memberCoupon3);
        couponRepository.saveAll(List.of(coupon1, coupon2, coupon3));

        //when
        List<Coupon> coupons = couponRepository.findByProductGroupIdWithMemberCoupons(coupon1.getProductGroupId());

        //then
        assertThat(coupons).hasSize(3);
        assertThat(coupons)
                .extracting(Coupon::getPrice, Coupon::getProductGroupId)
                .contains(
                        Tuple.tuple(1000L, 111111L),
                        Tuple.tuple(2000L, 111111L),
                        Tuple.tuple(3000L, 111111L));

        assertThat(coupons.get(0).getMemberCoupons()).extracting(MemberCoupon::getMember).contains(member);

    }

    @DisplayName("쿠폰아이디를 통해 쿠폰을 조회하는데 MemberCoupon 도 같이 fetch join 하여 조회할 수 있다")
    @Test
    void findByIdWithMemberCoupons(){
        //given
        Member member1 = Member.builder().name("홍길동").build();
        Member member2 = Member.builder().name("김길동").build();
        Member member3 = Member.builder().name("박길동").build();
        memberRepository.saveAll(List.of(member1, member2, member3));

        Coupon coupon = Coupon.builder().price(1000L).count(999L).build();

        MemberCoupon memberCoupon1 = MemberCoupon.builder().member(member1).build();
        MemberCoupon memberCoupon2 = MemberCoupon.builder().member(member2).build();
        MemberCoupon memberCoupon3 = MemberCoupon.builder().member(member3).build();

        coupon.addMemberCoupon(memberCoupon1);
        coupon.addMemberCoupon(memberCoupon2);
        coupon.addMemberCoupon(memberCoupon3);
        couponRepository.save(coupon);

        //when
        Optional<Coupon> savedCoupon = couponJpaRepository.findByIdWithMemberCoupons(coupon.getId());

        //then
        assertThat(savedCoupon.get().getMemberCoupons()).hasSize(3);
        assertThat(savedCoupon.get().getMemberCoupons())
                .extracting(MemberCoupon::getMember)
                .contains(member1, member2, member3);
    }
}
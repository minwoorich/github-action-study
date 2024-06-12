package com.objects.marketbridge.domains.coupon.infra;

import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.coupon.service.port.CouponRepository;
import com.objects.marketbridge.domains.coupon.mock.FakeCouponRepository;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.mock.FakeMemberRepository;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.product.mock.FakeProductRepository;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class FakeCouponRepositoryTest {

    CouponRepository couponRepository = new FakeCouponRepository();
    ProductRepository productRepository = new FakeProductRepository();
    MemberRepository memberRepository = new FakeMemberRepository();
    @AfterEach
    void clear() {
        couponRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
    }


    @DisplayName("DB에 저장된 모든 쿠폰을 조회 할 수 있다")
    @Test
    void findAll(){
        //given
        couponRepository.save(Coupon.builder().price(1000L).build());
        couponRepository.save(Coupon.builder().price(2000L).build());
       couponRepository.save(Coupon.builder().price(3000L).build());

        //when
        List<Coupon> coupons = couponRepository.findAll();

        //then
        assertThat(coupons).hasSize(3);
        assertThat(coupons)
                .extracting(c -> c.getPrice())
                .containsExactly(1000L, 2000L, 3000L);
    }

    @DisplayName("상품에 등록된 쿠폰이 없을 경우 빈 List 객체를 반환한다.")
    @Test
    void findAll_empty(){
        //given
        // 아무것도 저장되어있지 않음

        //when
        List<Coupon> coupons = couponRepository.findAll();

        //then
        assertThat(coupons).isEmpty();
        assertThat(coupons).isNotNull();
        assertThat(coupons).isInstanceOf(List.class);
    }

    @DisplayName("상품그룹아이디를 통해 상품에 등록된 모든 쿠폰들을 조회할 수 있다.")
    @Test
    void findByProductGroupId(){
        //given
        Coupon coupon1 = couponRepository.save(Coupon.builder().productGroupId(111111L).price(1000L).build());
        Coupon coupon2 = couponRepository.save(Coupon.builder().productGroupId(111111L).price(2000L).build());
        Coupon coupon3 = couponRepository.save(Coupon.builder().productGroupId(111111L).price(3000L).build());
        couponRepository.saveAll(List.of(coupon1, coupon2, coupon3));

        //when
        List<Coupon> coupons = couponRepository.findByProductGroupId(111111L);

        //then
        assertThat(coupons).hasSize(3);
        assertThat(coupons).extracting(Coupon::getProductGroupId, Coupon::getPrice)
                .contains(
                        Tuple.tuple(111111L, 1000L),
                        Tuple.tuple(111111L, 2000L),
                        Tuple.tuple(111111L, 3000L)
                );
    }

    @DisplayName("쿠폰을 조회할 때 MemberCoupons 들도 같이 fetch join 해서 가져온다.")
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
        Coupon savedCoupon = couponRepository.findByIdWithMemberCoupons(coupon.getId());

        //then
        assertThat(savedCoupon.getMemberCoupons()).hasSize(3);
        assertThat(savedCoupon.getMemberCoupons())
                .extracting(MemberCoupon::getMember)
                .contains(member1, member2, member3);
    }

    @DisplayName("해당 하는 쿠폰이 없는경우 에러를 발생 시킨다")
    @Test
    void findByIdWithMemberCoupons_error(){
        //given
        //when
        Throwable thrown = catchThrowable(() -> couponRepository.findByIdWithMemberCoupons(1L));

        //then
        assertThat(thrown).isInstanceOf(JpaObjectRetrievalFailureException.class)
                .hasMessage("해당 쿠폰 엔티티가 존재하지 않습니다. 입력 id = " + 1L);
    }
}
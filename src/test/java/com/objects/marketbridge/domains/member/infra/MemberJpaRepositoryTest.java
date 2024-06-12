//package com.objects.marketbridge.domain.member.repository;
//
//import com.objects.marketbridge.domain.address.repository.AddressRepository;
//import com.objects.marketbridge.domain.model.Address;
//import com.objects.marketbridge.domain.model.AddressValue;
//import com.objects.marketbridge.domain.model.Member;
//import com.objects.marketbridge.domain.model.Point;
//import com.objects.marketbridge.domain.point.repository.PointRepository;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class MemberJpaRepositoryTest {
//    @Autowired MemberJpaRepository memberJpaRepository;
//    @Autowired AddressRepository addressRepository;
//    @Autowired PointRepository pointRepository;
//
//
//    @BeforeEach
//    void init() {
//        memberJpaRepository.save(createMember("test@email.com"));
//    }
//
//    @AfterEach
//    void tearDown() {
//        memberJpaRepository.deleteAllInBatch();
//        addressRepository.deleteAllInBatch();
//        pointRepository.deleteAllInBatch();
//    }
//    @Test
//    void findByIdWithAddresses() {
//        // given
//        Member member = memberJpaRepository.findByEmail("test@email.com").orElseThrow(EntityNotFoundException::new);
//
//        Address address1 = createAddress("서울", member);
//        Address address2 = createAddress("부산", member);
//
//        address1.setMember(member);
//        address2.setMember(member);
//        addressRepository.saveAll(List.of(address1, address2));
//
//        List<String> expectedCities = List.of("서울", "부산");
//
//        //when
//        Member memberWithAddresses = memberJpaRepository.findByIdWithAddresses(member.getId()).orElseThrow(EntityNotFoundException::new);
//        List<Address> addresses = memberWithAddresses.getAddresses();
//
//        //then
//        assertThat(memberWithAddresses.getEmail()).isEqualTo("test@email.com");
//        assertThat(addresses).hasSize(2);
//        List<String> actualCities = addresses.stream().map(address -> address.getAddressValue().getCity()).toList();
//
//        assertThat(actualCities).containsExactlyInAnyOrderElementsOf(expectedCities);
//    }
//
//
//    @Test
//    void findByIdWithPoint(){
//        //given
//        Member member = memberJpaRepository.findByEmail("test@email.com").orElseThrow(() -> new EntityNotFoundException("엔티티가 존재하지 않습니다"));
//        Point point = createPoint(member, 4500L);
//        pointRepository.save(point);
//
//        //when
//        Member findMember = memberJpaRepository.findByIdWithPoint(member.getId()).orElseThrow(() -> new EntityNotFoundException("엔티티가 존재하지 않습니다"));
//        Point findPoint = findMember.getPoint();
//
//        //then
//        assertThat(findMember.getEmail()).isEqualTo("test@email.com");
//        assertThat(findPoint.getBalance()).isEqualTo(4500L);
//
//    }
//
//    private Point createPoint(Member member, Long balance) {
//        Point point = Point.builder().balance(balance).member(member).build();
//        point.setMember(member);
//
//        return point;
//    }
//
//    private Address createAddress(String city, Member member) {
//        return Address.builder()
//                .addressValue(AddressValue.builder()
//                        .city(city)
//                        .build())
//                .member(member)
//                .build();
//    }
//
//    private Member createMember(String email) {
//        return Member.builder()
//                .email(email)
//                .build();
//    }
//}
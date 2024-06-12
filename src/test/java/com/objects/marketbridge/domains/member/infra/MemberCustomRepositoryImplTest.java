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
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@Transactional
//class MemberCustomRepositoryImplTest {
//
//    @Autowired
//    MemberRepository memberRepository;
//    @Autowired
//    AddressRepository addressRepository;
//    @Autowired
//    PointRepository pointRepository;
//
//
//    @BeforeEach
//    void init() {
//        memberRepository.save(createMember("test@email.com"));
//    }
//
//    @AfterEach
//    void tearDown() {
//        memberRepository.deleteAllInBatch();
//        addressRepository.deleteAllInBatch();
//        pointRepository.deleteAllInBatch();
//    }
//    @Test
//    void findByIdWithPointAndAddresses() {
//        //given
//        Member member = memberRepository.findByEmail("test@email.com").orElseThrow(EntityNotFoundException::new);
//        Address address1 = createAddress("서울", member);
//        Address address2 = createAddress("부산", member);
//
//        address1.setMember(member);
//        address2.setMember(member);
//        addressRepository.saveAll(List.of(address1, address2));
//
//        Point point = createPoint(member, 4500L);
//        pointRepository.save(point);
//
//        //when
//        Member findMember = memberRepository.findByIdWithPointAndAddresses(member.getId());
//        List<Address> addresses = findMember.getAddresses();
//
//        //then
//        assertThat(findMember.getEmail()).isEqualTo("test@email.com");
//        assertThat(findMember.getPoint().getBalance()).isEqualTo(4500L);
//        List<String> actualCities = addresses.stream().map(address -> address.getAddressValue().getCity()).toList();
//        assertThat(actualCities).containsExactlyInAnyOrderElementsOf(List.of("서울", "부산"));
//    }
//
//    private Member createMember(String email) {
//        return Member.builder()
//                .email(email)
//                .build();
//    }
//    private Address createAddress(String city, Member member) {
//        return Address.builder()
//                .addressValue(AddressValue.builder()
//                        .city(city)
//                        .build())
//                .member(member)
//                .build();
//    }
//
//    private Point createPoint(Member member, Long balance) {
//        Point point = Point.builder().balance(balance).member(member).build();
//        point.setMember(member);
//
//        return point;
//    }
//}
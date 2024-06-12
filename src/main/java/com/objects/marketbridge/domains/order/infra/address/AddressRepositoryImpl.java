package com.objects.marketbridge.domains.order.infra.address;

import com.objects.marketbridge.domains.member.domain.Address;
import com.objects.marketbridge.domains.order.service.port.AddressRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AddressRepositoryImpl implements AddressRepository {

    private final AddressJpaRepository addressJpaRepository;

    @Override
    public Address findDefaultAddress(Long memberId) {
        return addressJpaRepository.findDefaultAddress(memberId).orElseThrow(() -> new EntityNotFoundException("설정된 기본배송지가 없습니다."));
    }

    @Override
    public Long countAddress(Long memberId) {
        return addressJpaRepository.countAddress(memberId);
    }

    @Override
    public Address findById(Long id) {
        return addressJpaRepository.findById(id).orElseThrow(() -> new JpaObjectRetrievalFailureException(new EntityNotFoundException("등록된 주소가 없습니다. 입력한 id = "+id)));
    }

    @Override
    public void deleteAllByIdInBatch(Long addressId) {
        addressJpaRepository.deleteAllByIdInBatch(List.of(addressId));
    }
    @Override
    public Address save(Address address) {
        return addressJpaRepository.save(address);
    }

    @Override
    public void deleteById(Long addressId) {
        addressJpaRepository.deleteById(addressId);
    }

    @Override
    public void saveAll(List<Address> addresses) {
        addressJpaRepository.saveAll(addresses);
    }

    @Override
    public List<Address> findByMemberId(Long memberId) {
        return addressJpaRepository.findByMemberId(memberId);
    }

    @Override
    public void deleteAllInBatch(){
        addressJpaRepository.deleteAllInBatch();
    }
}

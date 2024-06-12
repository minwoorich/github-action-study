package com.objects.marketbridge.domains.order.service.port;

import com.objects.marketbridge.domains.member.domain.Address;

import java.util.List;

public interface AddressRepository {

    Address findById(Long id);
    List<Address> findByMemberId(Long memberId);

    Address findDefaultAddress(Long memberId);

    Long countAddress(Long memberId);
    Address save(Address address);

    void deleteById(Long addressId);

    void deleteAllByIdInBatch(Long addressId);

    void saveAll(List<Address> addresses);

    void deleteAllInBatch();
}

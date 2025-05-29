package com.example.pizzashop.services;

import com.example.pizzashop.dtos.MembershipDTO;
import com.example.pizzashop.models.Membership;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IMembershipService {
    Membership getByMemberId(Long id) throws Exception;

    Membership createMembership(MembershipDTO membershipDTO);

    Membership updateMembership(Long id, MembershipDTO membershipDTO) throws Exception;

    List<Membership> getAllMemberships();

    Membership getMemberByPhone(String phone) throws Exception;
    void deleteMembership(String phone) throws Exception;

}

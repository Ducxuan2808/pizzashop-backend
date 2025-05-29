package com.example.pizzashop.services;

import com.example.pizzashop.dtos.MembershipDTO;
import com.example.pizzashop.exceptions.DataNotFoundException;
import com.example.pizzashop.models.Membership;
import com.example.pizzashop.models.User;
import com.example.pizzashop.repositories.MembershipRepository;
import com.example.pizzashop.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MembershipService implements  IMembershipService{

    private final MembershipRepository membershipRepository;

    private final UserRepository userRepository;
    @Override
    public Membership getByMemberId(Long id) throws Exception{
        Optional<Membership> optionalMember = membershipRepository.findById(id);
        if(optionalMember.isPresent()){
            return optionalMember.get();
        }
        throw new DataNotFoundException("Cannot find user with id = "+ id);
    }

    @Override
    @Transactional
    public Membership createMembership(MembershipDTO membershipDTO){

        Membership existingMember = membershipRepository.getMembershipByPhone(membershipDTO.getPhone()).orElse(null);
        if(existingMember == null){
            Membership membership = Membership.builder()
                    .phone(membershipDTO.getPhone())
                    .totalSpent(membershipDTO.getTotalSpent())
                    .membershipTier(membershipDTO.getMembershipTier())
                    .discountRate(membershipDTO.getDiscountRate())
                    .active(true)
                    .build();
            return membershipRepository.save(membership);
        }
        return null;

    }

    @Override
    @Transactional
    public Membership updateMembership(Long id, MembershipDTO membershipDTO) throws Exception{
        Membership existingMembership = getByMemberId(id);
        if(existingMembership!=null){
            //copy cac thuoc tinh DTO->product
            //co the sd ModelMapper
            User existingUser = userRepository.findByPhone(membershipDTO.getPhone())
                    .orElse(null);
            if(existingUser != null){
                existingMembership.setPhone(existingUser.getPhone());
            } else {
                existingMembership.setPhone(membershipDTO.getPhone());
            }
            existingMembership.setTotalSpent(membershipDTO.getTotalSpent());
            existingMembership.setMembershipTier(membershipDTO.getMembershipTier());
            existingMembership.setDiscountRate(membershipDTO.getDiscountRate());
            return membershipRepository.save(existingMembership);
        }
        return null;
    }

    @Override
    public List<Membership> getAllMemberships() {
        return membershipRepository.getAllMembership();
    }

    @Override
    public Membership getMemberByPhone(String phone) throws Exception {
        if(phone != null){
            Membership existingMember = membershipRepository.getMembershipByPhone(phone).orElse(null);
            if(existingMember!=null){
                return existingMember;
            }
            else{
                throw new Exception("Member not found");
            }
        }
        return null;
    }

    @Override
    public void deleteMembership(String phone) throws Exception {
        Membership existingMember = membershipRepository.findMembershipByPhone(phone).orElse(null);
        if(existingMember != null){
            existingMember.setActive(false);
            membershipRepository.save(existingMember);
        }
    }


}

package com.example.pizzashop.repositories;

import com.example.pizzashop.models.Membership;
import com.example.pizzashop.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership,Long> {
    @Query("SELECT m FROM Membership m " +
            "WHERE m.phone = :phone AND m.active = true")
    Optional<Membership> getMembershipByPhone(String phone);

    Optional<Membership> findMembershipByPhone(String phone);

    @Query("SELECT m FROM Membership m " +
            "WHERE m.active = true")
    List<Membership> getAllMembership();
}

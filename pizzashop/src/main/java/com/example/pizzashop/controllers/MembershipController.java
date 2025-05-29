package com.example.pizzashop.controllers;

import com.example.pizzashop.components.LocalizationUtils;
import com.example.pizzashop.dtos.MembershipDTO;
import com.example.pizzashop.models.Membership;
import com.example.pizzashop.responses.MembershipResponse;
import com.example.pizzashop.services.IMembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("membership")
@RequiredArgsConstructor
public class MembershipController {
    private final IMembershipService membershipService;
    private final LocalizationUtils localizationUtils;

    @GetMapping("")
    public ResponseEntity<List<Membership>> getAllMemberships() {
        List<Membership> memberships = membershipService.getAllMemberships();
        return ResponseEntity.ok(memberships);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMembership(@PathVariable Long id,
                                              @RequestBody MembershipDTO membershipDTO) {
        try {
            Membership existingMembership = membershipService.updateMembership(id, membershipDTO);
            MembershipResponse membershipResponse = MembershipResponse.builder()
                    .membership(existingMembership)
                    .message("Cập nhật thành công")
                    .build();
            return ResponseEntity.ok(membershipResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<?> getMemberByUserPhone(@PathVariable("phone") String phone) {
        try {
            Membership existingMembership = membershipService.getMemberByPhone(phone);
            MembershipResponse membershipResponse = MembershipResponse.builder()
                    .membership(existingMembership)
                    .message("Lấy dữ liệu thành công")
                    .build();
            return ResponseEntity.ok(membershipResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMemberById(@PathVariable("id") Long member) {
        try {
            Membership existingMembership = membershipService.getByMemberId(member);
            return ResponseEntity.ok(existingMembership);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createMembership(
            @Valid @RequestBody MembershipDTO membershipDTO,
            BindingResult result) {
        MembershipResponse membershipResponse = new MembershipResponse();
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            membershipResponse.setMessage(localizationUtils.getLocalizationMessage("Thêm thành viên không thành công"));
            membershipResponse.setErrors(errorMessages);
            return ResponseEntity.badRequest().body(membershipResponse);
        }
        Membership membership = membershipService.createMembership(membershipDTO);
        if(membership == null){
            return ResponseEntity.badRequest().body("Số điện thoại đã tồn tại");
        }
        membershipResponse.setMessage("Thêm thành viên thành công");
        membershipResponse.setMembership(membership);
        return ResponseEntity.ok(membershipResponse);
    }

    @DeleteMapping("/{phone}")
    @Transactional
    public ResponseEntity<String> deleteReview(@PathVariable String phone){
        try {
            membershipService.deleteMembership(phone);
            return ResponseEntity.ok("Thành viên đã xóa thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}


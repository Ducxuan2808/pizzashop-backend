package com.example.pizzashop.controllers;

import com.example.pizzashop.components.LocalizationUtils;
import com.example.pizzashop.dtos.PizzaDTO;
import com.example.pizzashop.dtos.SizeDTO;
import com.example.pizzashop.models.BaseType;
import com.example.pizzashop.models.Pizza;
import com.example.pizzashop.models.Size;
import com.example.pizzashop.responses.PizzaResponse;
import com.example.pizzashop.responses.SizeResponse;
import com.example.pizzashop.services.ISizeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("sizes")
@RequiredArgsConstructor
public class SizeController {
    private final ISizeService sizeService;
    private final LocalizationUtils localizationUtils;

    @GetMapping("")
    public ResponseEntity<List<Size>> getAllSizes(){
        List<Size> sizes =  sizeService.getAllSizes();
        return ResponseEntity.ok(sizes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSize(@PathVariable Long id,
                                           @RequestBody SizeDTO sizeDTO){
        try {
            Size existingSize =  sizeService.updateSize(id,sizeDTO);
            return ResponseEntity.ok(existingSize);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSizeById(@PathVariable("id") Long sizeId){
        try {
            Size existingSize =  sizeService.getSizeById(sizeId);
            return ResponseEntity.ok(existingSize);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping( "")
    public ResponseEntity<?> createSize(
            @Valid @RequestBody SizeDTO sizeDTO,
            BindingResult result){
        try{
            Size newSize =  sizeService.createSize(sizeDTO);
            if (newSize != null) {
                return ResponseEntity.ok(newSize);
            }
            else {
                return ResponseEntity.badRequest().body("Tên kích thước đã tồn tại");
            }
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSize(@PathVariable Long id){
        try {
            sizeService.deleteSize(id);
            return ResponseEntity.ok("Xóa thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

}

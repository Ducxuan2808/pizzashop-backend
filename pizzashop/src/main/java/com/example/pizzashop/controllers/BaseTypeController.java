package com.example.pizzashop.controllers;

import com.example.pizzashop.dtos.BaseTypeDTO;
import com.example.pizzashop.models.BaseType;
import com.example.pizzashop.models.Pizza;
import com.example.pizzashop.services.IBaseTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("types")
@RequiredArgsConstructor
public class BaseTypeController {
    private final IBaseTypeService baseTypeService;

    @GetMapping("")
    public ResponseEntity<List<BaseType>> getAllBaseTypes(){
        List<BaseType> baseTypes =  baseTypeService.getAllBaseTypes();
        return ResponseEntity.ok(baseTypes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateType(@PathVariable Long id,
                                        @RequestBody BaseTypeDTO baseTypeDTO){
        try {
            BaseType existingType =  baseTypeService.updateBaseType(id,baseTypeDTO);
            return ResponseEntity.ok(existingType);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTypeById(@PathVariable("id") Long typeId){
        try {
            BaseType existingType =  baseTypeService.getBaseTypeById(typeId);
            return ResponseEntity.ok(existingType);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping()
    public ResponseEntity<?> createBaseType(@RequestBody BaseTypeDTO baseTypeDTO){
        try{
            BaseType newType =  baseTypeService.createBaseType(baseTypeDTO);
            if (newType != null) {
                return ResponseEntity.ok(newType);
            }
            else {
                return ResponseEntity.badRequest().body("Tên phân loại đã tồn tại");
            }
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteType(@PathVariable Long id){
        try {
            baseTypeService.deleteType(id);
            return ResponseEntity.ok("Phân loại đã xóa thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

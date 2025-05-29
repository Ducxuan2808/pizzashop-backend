package com.example.pizzashop.services;

import com.example.pizzashop.dtos.SizeDTO;
import com.example.pizzashop.models.BaseType;
import com.example.pizzashop.models.Pizza;
import com.example.pizzashop.models.Size;
import com.example.pizzashop.repositories.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SizeService implements ISizeService{
    private final SizeRepository sizeRepository;
    @Override
    public List<Size> getAllSizes() {
        return sizeRepository.getAll();
    }

    @Override
    public Size getSizeById(Long id) {
        return sizeRepository.findById(id).orElseThrow(()->new RuntimeException("Size not found"));
    }

    @Override
    @Transactional
    public Size updateSize(Long id, SizeDTO sizeDTO) throws Exception {
        Size existingSize = getSizeById(id);
        if(existingSize != null){
            existingSize.setSizeName(sizeDTO.getSizeName());
            existingSize.setPriceMultiplier(sizeDTO.getPriceMultiplier());
            return sizeRepository.save(existingSize);
        }
        return null;
    }

    @Override
    public Size createSize(SizeDTO sizeDTO) throws Exception {
        Size existingSize = sizeRepository.findBySizeName(sizeDTO.getSizeName())
                .orElse(null);
        if(existingSize == null){
            Size size = Size.builder()
                    .sizeName(sizeDTO.getSizeName())
                    .priceMultiplier(sizeDTO.getPriceMultiplier())
                    .active(true)
                    .build();
            return sizeRepository.save(size);
        }
        return null;
    }

    @Override
    public void deleteSize(long id) throws Exception {
        Size existingSize = getSizeById(id);
        existingSize.setActive(false);
        sizeRepository.save(existingSize);
    }

}

package com.example.pizzashop.services;

import com.example.pizzashop.dtos.BaseTypeDTO;
import com.example.pizzashop.dtos.SizeDTO;
import com.example.pizzashop.exceptions.DataNotFoundException;
import com.example.pizzashop.models.BaseType;
import com.example.pizzashop.models.Pizza;
import com.example.pizzashop.models.Size;
import com.example.pizzashop.repositories.BaseTypeRepository;
import com.example.pizzashop.repositories.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BaseTypeService implements IBaseTypeService{
    private final BaseTypeRepository baseTypeRepository;
    @Override
    public BaseType getBaseTypeById(Long id) {
        return baseTypeRepository.findById(id).orElseThrow(()->new RuntimeException("Type not found"));
    }

    @Override
    public List<BaseType> getAllBaseTypes() {
        return baseTypeRepository.getAll();
    }

    @Override
    @Transactional
    public BaseType updateBaseType(Long id, BaseTypeDTO baseTypeDTO) throws Exception {
        BaseType existingType = getBaseTypeById(id);
        existingType.setBaseName(baseTypeDTO.getBaseName());
        existingType.setPrice(baseTypeDTO.getPrice());
        baseTypeRepository.save(existingType);
        return existingType;
    }

    @Override
    public BaseType createBaseType(BaseTypeDTO baseTypeDTO) throws Exception {
        BaseType existingBaseType = baseTypeRepository.findByBaseName(baseTypeDTO.getBaseName())
                .orElse(null);
        if(existingBaseType == null){
            BaseType baseType = BaseType.builder()
                    .baseName(baseTypeDTO.getBaseName())
                    .price(baseTypeDTO.getPrice())
                    .active(true)
                    .build();
            return baseTypeRepository.save(baseType);
        }
        return null;
    }

    @Override
    public void deleteType(long id) throws Exception {
        BaseType existingtype = getBaseTypeById(id);
        existingtype.setActive(false);
        baseTypeRepository.save(existingtype);

    }
}

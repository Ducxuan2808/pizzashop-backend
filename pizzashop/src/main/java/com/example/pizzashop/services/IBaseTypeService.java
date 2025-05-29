package com.example.pizzashop.services;

import com.example.pizzashop.dtos.BaseTypeDTO;
import com.example.pizzashop.models.BaseType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IBaseTypeService {
    List<BaseType> getAllBaseTypes();
    BaseType getBaseTypeById(Long id);

    BaseType updateBaseType(Long id, BaseTypeDTO baseTypeDTO) throws Exception;

    BaseType createBaseType(BaseTypeDTO baseTypeDTO) throws Exception;
    void deleteType(long id) throws Exception;
}

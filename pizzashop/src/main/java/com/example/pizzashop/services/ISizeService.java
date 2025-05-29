package com.example.pizzashop.services;

import com.example.pizzashop.dtos.SizeDTO;
import com.example.pizzashop.models.Size;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ISizeService {
    List<Size> getAllSizes();

    Size getSizeById(Long id);

    Size updateSize(Long id, SizeDTO sizeDTO) throws Exception;

    Size createSize(SizeDTO sizeDTO) throws Exception;
    void deleteSize(long id) throws Exception;
}


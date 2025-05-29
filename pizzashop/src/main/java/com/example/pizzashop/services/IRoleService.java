package com.example.pizzashop.services;

import com.example.pizzashop.models.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IRoleService {
    List<Role> getAllRoles();
}

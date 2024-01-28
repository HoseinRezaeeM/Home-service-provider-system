package com.example.homeserviceprovider.service.base;

import com.example.homeserviceprovider.base.service.BaseEntityService;
import com.example.homeserviceprovider.domain.user.Users;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface BaseUsersService<T extends Users> extends BaseEntityService<T,Long> {
      Optional<T> findByUsername(String email);
}

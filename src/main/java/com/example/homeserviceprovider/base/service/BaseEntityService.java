package com.example.homeserviceprovider.base.service;

import com.example.homeserviceprovider.base.domain.BaseEntity;
import org.springframework.stereotype.Service;


import java.io.Serializable;
import java.util.List;
import java.util.Optional;


public interface BaseEntityService<T extends BaseEntity<ID>,ID extends Serializable> {
      void save(T t);

      void delete(T t);

      Optional<T> findById(ID id);

      List<T> findAll();

      boolean isExistById(ID id);
}

package com.example.homeserviceprovider.base.service.impl;

import com.example.homeserviceprovider.base.domain.BaseEntity;
import com.example.homeserviceprovider.base.repository.BaseEntityRepository;
import com.example.homeserviceprovider.base.service.BaseEntityService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.Serializable;
import java.util.List;
import java.util.Optional;


public  class BaseEntityServiceImpl<T extends BaseEntity<ID>
       , ID extends Serializable
       , R extends BaseEntityRepository<T, ID>>
       implements BaseEntityService<T, ID> {

      protected final R repository;

      public BaseEntityServiceImpl(R repository) {
            this.repository = repository;
      }

      @Override
      @Transactional
      public void save(T t) {
            repository.save(t);
      }

      @Override
      @Transactional
      public void delete(T t) {
            repository.delete(t);

      }

      @Override
      public Optional<T> findById(ID id) {
            return repository.findById(id);
      }

      @Override
      public List<T> findAll() {
            return repository.findAll();
      }

      @Override
      public boolean isExistById(ID id) {
            return repository.existsById(id);
      }
}

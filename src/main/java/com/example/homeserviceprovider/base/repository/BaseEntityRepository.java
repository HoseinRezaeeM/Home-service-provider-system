package com.example.homeserviceprovider.base.repository;

import com.example.homeserviceprovider.base.domain.BaseEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.io.Serializable;
import java.util.List;
import java.util.Optional;


public interface BaseEntityRepository<T extends BaseEntity<ID>,ID extends Serializable> extends JpaRepository<T,ID> {
      @Override
      List<T> findAll();


      @Override
      <S extends T> S save(S entity);

      @Override
      Optional<T> findById(ID id);

      @Override
      boolean existsById(ID id);

      @Override
      void deleteById(ID id);

      @Override
      void delete(T entity);
}

package com.example.homeserviceprovider.repository.base;


import com.example.homeserviceprovider.base.repository.BaseEntityRepository;
import com.example.homeserviceprovider.domain.user.Users;
import com.example.homeserviceprovider.domain.user.enums.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends BaseEntityRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    @Query("select u from Users u where u.role =:role ")
    List<Users> findAllByRole(Role role);

}

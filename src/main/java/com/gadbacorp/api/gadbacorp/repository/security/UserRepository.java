package com.gadbacorp.api.gadbacorp.repository.security;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.gadbacorp.entity.security.User;



public interface UserRepository extends JpaRepository<User, Long> {

    Optional <User> findByUsername(String username);


}

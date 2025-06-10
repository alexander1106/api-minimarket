package com.gadbacorp.api.repository.security;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gadbacorp.api.entity.security.Permiso;
import com.gadbacorp.api.entity.security.User;



public interface UserRepository extends JpaRepository<User, Long> {

    Optional <User> findByUsername(String username);

@Query("""
    SELECT u FROM User u
    JOIN FETCH u.rol r
    JOIN FETCH r.permisos
    WHERE u.username = :username
""")
Optional<User> findByUsernameWithRolAndPermisos(@Param("username") String username);


}

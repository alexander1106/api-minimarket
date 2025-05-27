package com.gadbacorp.api.repository.users;

import org.springframework.data.repository.CrudRepository;

import com.gadbacorp.api.entity.user.Rol;

public interface RolRepository extends CrudRepository<Rol, Integer> {
    // Additional query methods can be defined here if needed

}

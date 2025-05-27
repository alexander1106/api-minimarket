package com.gadbacorp.api.repository.users;

import org.springframework.data.repository.CrudRepository;

import com.gadbacorp.api.entity.user.Usuarios;

public interface UserRepository extends CrudRepository<Usuarios, Integer> {


}

package com.gadbacorp.api.entity.user;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

public class Usuarios {

    @Entity
    @Table(name = "usuarios")
    public class Usuario {
        private Integer id;

        @Column(unique = true)
        private String username;

        private String password;
        
        @Column(unique = true)
        private String email;



        @ManyToMany
        @JoinTable(
            name = "usuario_roles", 
            joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role_id"})}
        )
        
        private List<Rol> roles;

        // Getters and Setters
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

}

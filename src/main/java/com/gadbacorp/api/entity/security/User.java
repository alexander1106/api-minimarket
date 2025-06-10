package com.gadbacorp.api.entity.security;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gadbacorp.api.repository.security.UserRepository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "usersapp")
@SQLDelete(sql = "UPDATE usersapp SET estado=0 WHERE id = ?")
@Where(clause = "estado=1")

public class User implements UserDetails {
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "estado", nullable = false)
    private Integer estado = 1; // 1 activo, 0 inactivo

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "rol_id")
        private Rol rol;


    @Column(name = "is_2fa_enabled")
    private Boolean is2faEnabled = false;

    @Column(name = "secret_2fa")
    private String secret2fa; // Este se usa si vas a usar TOTP tipo Google Authenticator

    @Column(name = "code_2fa")
    private String code2fa;

    @Column(name = "code_2fa_expiration")
    private LocalDateTime code2faExpiration;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "token_jwt", columnDefinition = "VARCHAR(4000)")
    private String tokenJwt;

    @Column(name = "token_expiracion")
    private LocalDateTime tokenExpiracion;
     @Column(name = "token_signature", columnDefinition = "VARCHAR(4000)")
    private String tokenSignature;
    

    public Boolean getIs2faEnabled() {
        return is2faEnabled;
    }

    public void setIs2faEnabled(Boolean is2faEnabled) {
        this.is2faEnabled = is2faEnabled;
    }

    public String getCode2fa() {
        return code2fa;
    }

    public void setCode2fa(String code2fa) {
        this.code2fa = code2fa;
    }

    public LocalDateTime getCode2faExpiration() {
        return code2faExpiration;
    }

    public void setCode2faExpiration(LocalDateTime code2faExpiration) {
        this.code2faExpiration = code2faExpiration;
    }

    public boolean is2faEnabled() {
        return is2faEnabled;
    }

    public void set2faEnabled(boolean is2faEnabled) {
        this.is2faEnabled = is2faEnabled;
    }

    public String getSecret2fa() {
        return secret2fa;
    }

    public void setSecret2fa(String secret2fa) {
        this.secret2fa = secret2fa;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (rol != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));
            List<Permiso> permisos = rol.getPermisos().stream().collect(Collectors.toList());
            for (Permiso permiso : permisos) {
                authorities.add(new SimpleGrantedAuthority(permiso.getNombre()));
            }
        }
        return authorities;
    }



    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getTokenJwt() {
        return tokenJwt;
    }

    public void setTokenJwt(String tokenJwt) {
        this.tokenJwt = tokenJwt;
    }

    public LocalDateTime getTokenExpiracion() {
        return tokenExpiracion;
    }

    public void setTokenExpiracion(LocalDateTime tokenExpiracion) {
        this.tokenExpiracion = tokenExpiracion;
    }

    public String getTokenSignature() {
        return tokenSignature;
    }

    public void setTokenSignature(String tokenSignature) {
        this.tokenSignature = tokenSignature;
    }


}

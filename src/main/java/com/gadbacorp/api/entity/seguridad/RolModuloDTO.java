package com.gadbacorp.api.entity.seguridad;


public class RolModuloDTO {
 private Integer id;
    private Integer rolId;
    private Integer moduloId;

    public RolModuloDTO() {}

    public RolModuloDTO(Integer id, Integer rolId, Integer moduloId) {
        this.id = id;
        this.rolId = rolId;
        this.moduloId = moduloId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRolId() {
        return rolId;
    }

    public void setRolId(Integer rolId) {
        this.rolId = rolId;
    }

    public Integer getModuloId() {
        return moduloId;
    }

    public void setModuloId(Integer moduloId) {
        this.moduloId = moduloId;
    }

    @Override
    public String toString() {
        return "RolModuloDTO [id=" + id + ", rolId=" + rolId + ", moduloId=" + moduloId + "]";
    }
} 
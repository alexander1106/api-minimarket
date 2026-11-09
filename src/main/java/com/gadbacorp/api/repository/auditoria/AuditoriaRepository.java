package com.gadbacorp.api.repository.auditoria;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gadbacorp.api.entity.auditoria.RegistroAuditoria;

public interface AuditoriaRepository extends JpaRepository<RegistroAuditoria, Long> {

    @Query("""
            select a from RegistroAuditoria a
            where a.fecha >= :desde and a.fecha <= :hasta
              and (:username is null or lower(a.username) like lower(concat('%', :username, '%')))
              and (:idEmpresa is null or a.idEmpresa = :idEmpresa)
              and (:accion is null or a.accion = :accion)
              and (:modulo is null or a.modulo = :modulo)
              and (:exito is null or a.exito = :exito)
              and (:q is null or lower(a.descripcion) like lower(concat('%', :q, '%'))
                   or lower(a.ruta) like lower(concat('%', :q, '%')))
            order by a.fecha desc, a.id desc
            """)
    Page<RegistroAuditoria> buscar(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta,
                                   @Param("username") String username, @Param("idEmpresa") Integer idEmpresa,
                                   @Param("accion") String accion, @Param("modulo") String modulo,
                                   @Param("exito") Boolean exito, @Param("q") String q, Pageable pagina);

    @Query("select a.accion, count(a) from RegistroAuditoria a where a.fecha >= :desde and a.fecha <= :hasta group by a.accion")
    List<Object[]> contarPorAccion(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("select a.modulo, count(a) from RegistroAuditoria a where a.fecha >= :desde and a.fecha <= :hasta group by a.modulo order by count(a) desc")
    List<Object[]> contarPorModulo(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("select a.username, count(a) from RegistroAuditoria a where a.fecha >= :desde and a.fecha <= :hasta and a.username is not null group by a.username order by count(a) desc")
    List<Object[]> contarPorUsuario(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta, Pageable pagina);

    @Query("select count(a) from RegistroAuditoria a where a.fecha >= :desde and a.fecha <= :hasta and a.exito = false")
    long contarFallidos(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("select distinct a.username from RegistroAuditoria a where a.username is not null order by a.username")
    List<String> usuarios();

    @Query("select distinct a.modulo from RegistroAuditoria a where a.modulo is not null order by a.modulo")
    List<String> modulos();

    @Modifying
    @Query("delete from RegistroAuditoria a where a.fecha < :limite")
    int borrarAnterioresA(@Param("limite") LocalDateTime limite);
}

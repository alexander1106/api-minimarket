package com.gadbacorp.api.repository.notificaciones;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gadbacorp.api.entity.notificaciones.Notificacion;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    @Query("""
            select n from Notificacion n
            where n.idUsuario = :idUsuario
              and (:soloNoLeidas = false or n.leida = false)
              and (:tipo is null or n.tipo = :tipo)
            order by n.fecha desc, n.idNotificacion desc
            """)
    Page<Notificacion> buscar(@Param("idUsuario") Integer idUsuario, @Param("soloNoLeidas") boolean soloNoLeidas,
                              @Param("tipo") String tipo, Pageable pagina);

    long countByIdUsuarioAndLeidaFalse(Integer idUsuario);

    Optional<Notificacion> findByIdNotificacionAndIdUsuario(Long idNotificacion, Integer idUsuario);

    boolean existsByIdUsuarioAndClaveAndLeidaFalse(Integer idUsuario, String clave);

    @Modifying
    @Query("update Notificacion n set n.leida = true, n.fechaLectura = :ahora where n.idUsuario = :idUsuario and n.leida = false")
    int marcarTodasLeidas(@Param("idUsuario") Integer idUsuario, @Param("ahora") LocalDateTime ahora);

    @Modifying
    @Query("delete from Notificacion n where n.idUsuario = :idUsuario and n.leida = true")
    int borrarLeidas(@Param("idUsuario") Integer idUsuario);

    @Modifying
    @Query("delete from Notificacion n where n.leida = true and n.fechaLectura < :limite")
    int borrarLeidasAnterioresA(@Param("limite") LocalDateTime limite);
}

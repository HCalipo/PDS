package com.tasku.core.infrastructure.persistence.jpa.repository;

import com.tasku.core.infrastructure.persistence.jpa.entity.TarjetaJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataTarjetaRepository extends JpaRepository<TarjetaJpaEntity, UUID> {
    @Query("select count(c) from TarjetaJpaEntity c where c.list.id = :listId")
    long countByListId(@Param("listId") UUID listId);

    @EntityGraph(attributePaths = {"labels"})
    @Query("select c from TarjetaJpaEntity c where c.list.id = :listId")
    List<TarjetaJpaEntity> findByListId(@Param("listId") UUID listId);

    @Override
    @EntityGraph(attributePaths = {"labels"})
    Optional<TarjetaJpaEntity> findById(UUID cardId);
}

package com.tasku.core.infrastructure.persistence.jpa.repository;

import com.tasku.core.infrastructure.persistence.jpa.entity.ListaTableroJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataListaTableroRepository extends JpaRepository<ListaTableroJpaEntity, UUID> {
}

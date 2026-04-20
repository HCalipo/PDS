package com.tasku.core.infrastructure.persistence.jpa.repository;

import com.tasku.core.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataUsuarioRepository extends JpaRepository<UsuarioJpaEntity, String> {
}

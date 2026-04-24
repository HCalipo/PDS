package com.tasku.core.infrastructure.persistence.jpa.mapper;

import com.tasku.core.domain.model.CuentaUsuario;
import com.tasku.core.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioJpaMapper {
    public UsuarioJpaEntity toJpa(CuentaUsuario domain) {
        UsuarioJpaEntity entity = new UsuarioJpaEntity();
        entity.setEmail(domain.email());
        entity.setRegistrationDate(domain.registrationDate());
        return entity;
    }

    public CuentaUsuario toDomain(UsuarioJpaEntity entity) {
        return new CuentaUsuario(entity.getEmail(), entity.getRegistrationDate());
    }
}



package com.tasku.core.infrastructure.persistence.jpa.mapper;

import com.tasku.core.domain.model.Usuario;
import com.tasku.core.domain.model.Email;
import com.tasku.core.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioJpaMapper {
    public UsuarioJpaEntity toJpa(Usuario domain) {
        UsuarioJpaEntity entity = new UsuarioJpaEntity();
        entity.setEmail(domain.email());
        entity.setRegistrationDate(domain.registrationDate());
        return entity;
    }

    public Usuario toDomain(UsuarioJpaEntity entity) {
        return new Usuario(new Email(entity.getEmail()), entity.getRegistrationDate());
    }
}



package com.tasku.core.infrastructure.persistence.jpa.adapter;

import com.tasku.core.domain.model.board.CuentaUsuario;
import com.tasku.core.domain.board.port.UsuarioStore;
import com.tasku.core.infrastructure.persistence.jpa.mapper.UsuarioJpaMapper;
import com.tasku.core.infrastructure.persistence.jpa.repository.SpringDataUsuarioRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUsuarioStoreAdapter implements UsuarioStore {
    private final SpringDataUsuarioRepository repository;
    private final UsuarioJpaMapper mapper;

    public JpaUsuarioStoreAdapter(SpringDataUsuarioRepository repository, UsuarioJpaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public CuentaUsuario save(CuentaUsuario user) {
        return mapper.toDomain(repository.save(mapper.toJpa(user)));
    }

    @Override
    public Optional<CuentaUsuario> findByEmail(String email) {
        return repository.findById(CuentaUsuario.normalizeEmail(email)).map(mapper::toDomain);
    }
}


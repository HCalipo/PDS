package com.tasku.core.infrastructure.persistence.jpa.adapter;

import com.tasku.core.domain.model.Usuario;
import com.tasku.core.domain.model.Email;
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
    public Usuario save(Usuario user) {
        return mapper.toDomain(repository.save(mapper.toJpa(user)));
    }

    @Override
    public Optional<Usuario> findByEmail(Email email) {
        return repository.findById(email.email()).map(mapper::toDomain);
    }
}



package com.tasku.core.infrastructure.persistence.jpa.adapter;

import com.tasku.core.domain.model.board.ListaTablero;
import com.tasku.core.domain.board.port.ListaTableroStore;
import com.tasku.core.infrastructure.persistence.jpa.mapper.TableroJpaMapper;
import com.tasku.core.infrastructure.persistence.jpa.repository.SpringDataListaTableroRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaListaTableroStoreAdapter implements ListaTableroStore {
    private final SpringDataListaTableroRepository repository;
    private final TableroJpaMapper tableroJpaMapper;

    public JpaListaTableroStoreAdapter(SpringDataListaTableroRepository repository, TableroJpaMapper tableroJpaMapper) {
        this.repository = repository;
        this.tableroJpaMapper = tableroJpaMapper;
    }

    @Override
    public Optional<ListaTablero> findById(UUID listId) {
        return repository.findById(listId).map(tableroJpaMapper::toDomain);
    }
}

